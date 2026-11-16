package com.practice.helpdesk.retrieval;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.jupiter.api.Test;

import com.practice.helpdesk.ingestion.Chunk;
import com.practice.helpdesk.ingestion.DocumentLoader;
import com.practice.helpdesk.ingestion.TextChunker;

class SampleKnowledgeBaseRetrievalTest {

    private final KeywordRetriever retriever = buildRetriever();

    @Test
    void findsTheLeavePolicyForCarryForwardQuestion() {
        List<RetrievedChunk> results = retriever.search("unused leave forward", 3);

        assertThat(results).isNotEmpty();
        assertThat(results.get(0).chunk().sourcePath()).isEqualTo("hr/leave-policy.md");
    }

    @Test
    void findsTheVpnPolicyForVpnQuestion() {
        List<RetrievedChunk> results = retriever.search("VPN install", 3);

        assertThat(results).isNotEmpty();
        assertThat(results.get(0).chunk().sourcePath()).isEqualTo("it/vpn-access.md");
    }

    private static KeywordRetriever buildRetriever() {
        TextChunker chunker = new TextChunker(100, 20);
        List<Chunk> chunks = new DocumentLoader("knowledge-base").load().stream()
                .flatMap(document -> chunker.chunk(document).stream())
                .toList();
        return new KeywordRetriever(chunks);
    }
}
