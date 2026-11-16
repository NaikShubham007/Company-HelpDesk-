package com.practice.helpdesk.api;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.jupiter.api.Test;

import com.practice.helpdesk.ingestion.Chunk;
import com.practice.helpdesk.rag.Answer;
import com.practice.helpdesk.retrieval.RetrievedChunk;

class AskResponseTest {

    @Test
    void copiesTheAnswerTextAndEachSourcePassage() {
        Chunk chunk = new Chunk("hr/leave-policy.md", "Annual Leave", "HR", 0, "Full-time staff get 20 days.");
        Answer answer = new Answer("You get 20 days [1].", List.of(new RetrievedChunk(chunk, 0.8)));

        AskResponse response = AskResponse.from(answer);

        assertThat(response.answer()).isEqualTo("You get 20 days [1].");
        assertThat(response.sources()).containsExactly(new AskResponse.Source(
                "hr/leave-policy.md", "Annual Leave", 0.8, "Full-time staff get 20 days."));
    }

    @Test
    void hasNoSourcesWhenTheAnswerUsedNone() {
        AskResponse response = AskResponse.from(new Answer("I don't know based on the company documents.", List.of()));

        assertThat(response.sources()).isEmpty();
    }
}
