package com.practice.helpdesk.api;

import java.util.List;

import com.practice.helpdesk.rag.Answer;
import com.practice.helpdesk.retrieval.RetrievedChunk;

public record AskResponse(String answer, List<Source> sources) {

    public static AskResponse from(Answer answer) {
        List<Source> sources = answer.sources().stream().map(Source::from).toList();
        return new AskResponse(answer.text(), sources);
    }

    public record Source(String path, String title, double score, String text) {

        static Source from(RetrievedChunk retrieved) {
            return new Source(
                    retrieved.chunk().sourcePath(),
                    retrieved.chunk().title(),
                    retrieved.score(),
                    retrieved.chunk().text());
        }
    }
}
