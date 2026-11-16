package com.practice.helpdesk.retrieval;

import java.util.List;

import com.practice.helpdesk.ingestion.Chunk;
import com.practice.helpdesk.ingestion.DocumentLoader;
import com.practice.helpdesk.ingestion.TextChunker;

final class SampleKnowledgeBase {

    private SampleKnowledgeBase() {
    }

    static List<Chunk> chunks() {
        TextChunker chunker = new TextChunker(100, 20);
        return new DocumentLoader("knowledge-base").load().stream()
                .flatMap(document -> chunker.chunk(document).stream())
                .toList();
    }

    static KeywordRetriever keywordRetriever() {
        return new KeywordRetriever(chunks());
    }
}
