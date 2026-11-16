package com.practice.helpdesk.retrieval;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.jupiter.api.Test;

import com.practice.helpdesk.ingestion.Chunk;

class KeywordRetrieverTest {

    private final KeywordRetriever retriever = new KeywordRetriever(List.of(
            chunk("it/password-reset.md", "Use the self-service portal to reset your password. Passwords expire every 90 days."),
            chunk("it/vpn-access.md", "Connect the VPN before opening any internal application."),
            chunk("hr/leave-policy.md", "Up to 5 unused leave days can be carried forward to the next year.")));

    @Test
    void ranksTheChunkWithMatchingWordsFirst() {
        List<RetrievedChunk> results = retriever.search("how do I reset my password", 3);

        assertThat(results).hasSize(1);
        assertThat(results.get(0).chunk().sourcePath()).isEqualTo("it/password-reset.md");
    }

    @Test
    void returnsAtMostTopKResults() {
        List<RetrievedChunk> results = retriever.search("password vpn leave", 2);

        assertThat(results).hasSize(2);
    }

    @Test
    void returnsNothingWhenTheQuestionSharesNoWords() {
        // Keyword search matches words, not meaning. Embeddings in Phase 5 are meant to close this gap.
        assertThat(retriever.search("I cannot get into my account", 3)).isEmpty();
    }

    @Test
    void returnsNothingForAnEmptyIndex() {
        assertThat(new KeywordRetriever(List.of()).search("password", 3)).isEmpty();
    }

    private static Chunk chunk(String sourcePath, String text) {
        return new Chunk(sourcePath, "Test", "IT", 0, text);
    }
}
