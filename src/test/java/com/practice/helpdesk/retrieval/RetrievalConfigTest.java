package com.practice.helpdesk.retrieval;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.Test;

import com.practice.helpdesk.ingestion.Chunk;
import com.practice.helpdesk.ingestion.DocumentLoader;
import com.practice.helpdesk.ingestion.KnowledgeDocument;
import com.practice.helpdesk.ingestion.TextChunker;

class RetrievalConfigTest {

    @Test
    void readsTheDocumentsOnceAndChunksThem() {
        DocumentLoader loader = mock(DocumentLoader.class);
        when(loader.load()).thenReturn(List.of(new KnowledgeDocument(
                "it/vpn.md", "VPN Access", "IT", "IT Service Desk", "2026-11-01", "w1 w2 w3")));

        List<Chunk> chunks = new RetrievalConfig().knowledgeChunks(loader, new TextChunker(100, 20));

        verify(loader, times(1)).load();
        assertThat(chunks).extracting(Chunk::text).containsExactly("w1 w2 w3");
    }
}
