package com.practice.helpdesk.retrieval;

import org.junit.jupiter.api.Test;

class RetrievalEvaluationTest {

    @Test
    void printsKeywordHitRates() {
        KeywordRetriever retriever = SampleKnowledgeBase.keywordRetriever();

        RetrievalEval.report("keyword wording",
                question -> retriever.search(question, RetrievalEval.TOP_K), RetrievalEval.WORDING_CASES);
        RetrievalEval.report("keyword paraphrase",
                question -> retriever.search(question, RetrievalEval.TOP_K), RetrievalEval.PARAPHRASE_CASES);
    }
}
