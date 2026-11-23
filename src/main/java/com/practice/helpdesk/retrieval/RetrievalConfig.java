package com.practice.helpdesk.retrieval;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;

import com.practice.helpdesk.ingestion.Chunk;
import com.practice.helpdesk.ingestion.DocumentLoader;
import com.practice.helpdesk.ingestion.TextChunker;

@Configuration
class RetrievalConfig {

    private static final Logger log = LoggerFactory.getLogger(RetrievalConfig.class);

    @Bean
    List<Chunk> knowledgeChunks(DocumentLoader documentLoader, TextChunker textChunker) {
        List<Chunk> chunks = documentLoader.load().stream()
                .flatMap(document -> textChunker.chunk(document).stream())
                .toList();
        log.info("Split the documents into {} chunks", chunks.size());
        return chunks;
    }

    // Lazy so the embedding index is built on the first question, not at startup.
    @Bean
    @Lazy
    EmbeddingRetriever embeddingRetriever(EmbeddingModel embeddingModel, List<Chunk> knowledgeChunks) {
        return new EmbeddingRetriever(embeddingModel, knowledgeChunks);
    }
}
