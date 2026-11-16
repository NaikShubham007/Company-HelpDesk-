package com.practice.helpdesk.retrieval;

import java.util.List;
import java.util.function.Function;

final class RetrievalEval {

    static final int TOP_K = 3;

    // Questions that reuse words from the policy text.
    static final List<EvalCase> WORDING_CASES = List.of(
            new EvalCase("How do I install the VPN client?", "it/vpn-access.md"),
            new EvalCase("My VPN keeps disconnecting", "it/vpn-access.md"),
            new EvalCase("What happens after too many failed sign-in attempts?", "it/password-reset.md"),
            new EvalCase("I forgot my password, how do I reset it?", "it/password-reset.md"),
            new EvalCase("How many days of annual leave do I get each year?", "hr/leave-policy.md"),
            new EvalCase("Can unused vacation days roll over to next year?", "hr/leave-policy.md"),
            new EvalCase("Do I need a doctor's note if I am sick for a week?", "hr/leave-policy.md"),
            new EvalCase("I lost my company laptop, what should I do?", "it/laptop-request.md"),
            new EvalCase("How often do I get a new laptop?", "it/laptop-request.md"),
            new EvalCase("Can I work from home on Fridays?", "hr/work-from-home.md"),
            new EvalCase("How much money can I get for a home office setup?", "hr/work-from-home.md"),
            new EvalCase("How long do I have to submit an expense claim?", "finance/expense-claims.md"),
            new EvalCase("What is the hotel limit for a business trip?", "finance/expense-claims.md"),
            new EvalCase("Is it allowed to use public AI tools with company documents?", "security/acceptable-use.md"),
            new EvalCase("I got a suspicious email asking me to log in, what do I do?", "security/acceptable-use.md"));

    // Questions written in everyday words that share few or no words with the policy text.
    static final List<EvalCase> PARAPHRASE_CASES = List.of(
            new EvalCase("My computer was taken from my car last night, who do I tell?", "it/laptop-request.md"),
            new EvalCase("I typed the wrong secret code too many times and got shut out", "it/password-reset.md"),
            new EvalCase("Am I allowed to skip the commute on some weekdays?", "hr/work-from-home.md"),
            new EvalCase("Can I keep client files in my own Google Drive?", "security/acceptable-use.md"),
            new EvalCase("What is the most the company will cover for a room while travelling?", "finance/expense-claims.md"));

    private RetrievalEval() {
    }

    static void report(String label, Function<String, List<RetrievedChunk>> search, List<EvalCase> cases) {
        int hitsAtOne = 0;
        int hitsAtK = 0;
        for (EvalCase evalCase : cases) {
            List<RetrievedChunk> top = search.apply(evalCase.question());
            boolean inTopOne = !top.isEmpty() && isExpected(top.get(0), evalCase);
            boolean inTopK = top.stream().anyMatch(result -> isExpected(result, evalCase));
            if (inTopOne) {
                hitsAtOne++;
            }
            if (inTopK) {
                hitsAtK++;
            }
            System.out.printf("[%s] top1=%s top%d=%s  %s%n",
                    label, inTopOne ? "HIT " : "MISS", TOP_K, inTopK ? "HIT " : "MISS", evalCase.question());
        }
        System.out.printf("[%s] hit@1: %d/%d, hit@%d: %d/%d%n",
                label, hitsAtOne, cases.size(), TOP_K, hitsAtK, cases.size());
    }

    private static boolean isExpected(RetrievedChunk result, EvalCase evalCase) {
        return result.chunk().sourcePath().equals(evalCase.expectedSource());
    }

    record EvalCase(String question, String expectedSource) {
    }
}
