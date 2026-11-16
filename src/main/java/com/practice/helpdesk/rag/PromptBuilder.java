package com.practice.helpdesk.rag;

import java.util.List;

import com.practice.helpdesk.retrieval.RetrievedChunk;

public final class PromptBuilder {

    public static final String SYSTEM_INSTRUCTIONS = """
            You are the company help desk assistant.
            Answer the question using only the numbered context passages.
            Cite the passages you used, for example [1].
            If the passages do not contain the answer, reply exactly: I don't know based on the company documents.
            """;

    private PromptBuilder() {
    }

    public static String userMessage(String question, List<RetrievedChunk> sources) {
        StringBuilder message = new StringBuilder("Context passages:\n\n");
        for (int i = 0; i < sources.size(); i++) {
            RetrievedChunk source = sources.get(i);
            message.append('[').append(i + 1).append("] (source: ")
                    .append(source.chunk().sourcePath()).append(")\n")
                    .append(source.chunk().text()).append("\n\n");
        }
        message.append("Question: ").append(question);
        return message.toString();
    }
}
