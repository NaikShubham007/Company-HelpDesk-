package com.practice.helpdesk.retrieval;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.practice.helpdesk.ingestion.Chunk;
import com.practice.helpdesk.ingestion.DocumentLoader;
import com.practice.helpdesk.ingestion.TextChunker;

@Configuration
class RetrievalConfig {

    @Bean
    KeywordRetriever keywordRetriever(DocumentLoader documentLoader, TextChunker textChunker) {
        List<Chunk> chunks = documentLoader.load().stream()
                .flatMap(document -> textChunker.chunk(document).stream())
                .toList();
        return new KeywordRetriever(chunks);
    }
}
