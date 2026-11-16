package com.practice.helpdesk.retrieval;

import java.util.List;

import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;

import com.practice.helpdesk.ingestion.Chunk;
import com.practice.helpdesk.ingestion.DocumentLoader;
import com.practice.helpdesk.ingestion.TextChunker;

@Configuration
class RetrievalConfig {

    @Bean
    KeywordRetriever keywordRetriever(DocumentLoader documentLoader, TextChunker textChunker) {
        return new KeywordRetriever(chunks(documentLoader, textChunker));
    }

    // Lazy so the embedding index is built on the first question, not at startup.
    @Bean
    @Lazy
    EmbeddingRetriever embeddingRetriever(
            EmbeddingModel embeddingModel, DocumentLoader documentLoader, TextChunker textChunker) {
        return new EmbeddingRetriever(embeddingModel, chunks(documentLoader, textChunker));
    }

    private static List<Chunk> chunks(DocumentLoader documentLoader, TextChunker textChunker) {
        return documentLoader.load().stream()
                .flatMap(document -> textChunker.chunk(document).stream())
                .toList();
    }
}
