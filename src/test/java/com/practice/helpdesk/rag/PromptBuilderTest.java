package com.practice.helpdesk.rag;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.jupiter.api.Test;

import com.practice.helpdesk.ingestion.Chunk;
import com.practice.helpdesk.retrieval.RetrievedChunk;

class PromptBuilderTest {

    @Test
    void numbersEachPassageAndNamesItsSource() {
        List<RetrievedChunk> sources = List.of(
                new RetrievedChunk(chunk("it/vpn-access.md", "Connect the VPN first."), 0.9),
                new RetrievedChunk(chunk("it/password-reset.md", "Reset via the portal."), 0.5));

        String message = PromptBuilder.userMessage("How do I connect?", sources);

        assertThat(message)
                .contains("[1] (source: it/vpn-access.md)")
                .contains("Connect the VPN first.")
                .contains("[2] (source: it/password-reset.md)")
                .contains("Reset via the portal.")
                .endsWith("Question: How do I connect?");
    }

    @Test
    void keepsTheQuestionWhenThereAreNoPassages() {
        String message = PromptBuilder.userMessage("Anything?", List.of());

        assertThat(message).doesNotContain("[1]").endsWith("Question: Anything?");
    }

    private static Chunk chunk(String sourcePath, String text) {
        return new Chunk(sourcePath, "Test", "IT", 0, text);
    }
}
