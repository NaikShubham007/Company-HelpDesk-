package com.practice.helpdesk.ingestion;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.List;

import org.junit.jupiter.api.Test;

class TextChunkerTest {

    @Test
    void splitsIntoOverlappingWordWindows() {
        TextChunker chunker = new TextChunker(4, 1);

        List<Chunk> chunks = chunker.chunk(document("w1 w2 w3 w4 w5 w6 w7 w8 w9 w10"));

        assertThat(chunks).extracting(Chunk::text).containsExactly(
                "w1 w2 w3 w4",
                "w4 w5 w6 w7",
                "w7 w8 w9 w10");
        assertThat(chunks).extracting(Chunk::index).containsExactly(0, 1, 2);
    }

    @Test
    void keepsSourceMetadataOnEveryChunk() {
        TextChunker chunker = new TextChunker(4, 1);

        List<Chunk> chunks = chunker.chunk(document("w1 w2 w3 w4 w5"));

        assertThat(chunks).allSatisfy(chunk -> {
            assertThat(chunk.sourcePath()).isEqualTo("it/vpn.md");
            assertThat(chunk.title()).isEqualTo("VPN Access");
            assertThat(chunk.department()).isEqualTo("IT");
        });
    }

    @Test
    void returnsOneChunkWhenDocumentIsShorterThanChunkSize() {
        TextChunker chunker = new TextChunker(4, 1);

        List<Chunk> chunks = chunker.chunk(document("w1 w2"));

        assertThat(chunks).extracting(Chunk::text).containsExactly("w1 w2");
    }

    @Test
    void returnsNoChunksForBlankBody() {
        TextChunker chunker = new TextChunker(4, 1);

        assertThat(chunker.chunk(document("   \n  "))).isEmpty();
    }

    @Test
    void rejectsOverlapNotSmallerThanChunkSize() {
        assertThatThrownBy(() -> new TextChunker(4, 4))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void startsANewChunkAtEachHeadingAndKeepsTheHeadingWithItsText() {
        TextChunker chunker = new TextChunker(100, 20);

        List<Chunk> chunks = chunker.chunk(document("# Policy\n\n## Leave\nGet 20 days.\n\n## Sick\nGet 10 days."));

        assertThat(chunks).extracting(Chunk::text).containsExactly(
                "Leave Get 20 days.",
                "Sick Get 10 days.");
        assertThat(chunks).extracting(Chunk::index).containsExactly(0, 1);
    }

    @Test
    void splitsALongSectionIntoOverlappingWindows() {
        TextChunker chunker = new TextChunker(4, 1);

        List<Chunk> chunks = chunker.chunk(document("## Long\nw1 w2 w3 w4 w5"));

        assertThat(chunks).extracting(Chunk::text).containsExactly(
                "Long w1 w2 w3",
                "w3 w4 w5");
    }

    private KnowledgeDocument document(String body) {
        return new KnowledgeDocument("it/vpn.md", "VPN Access", "IT", "IT Service Desk", "2026-11-01", body);
    }
}
