package com.practice.helpdesk.ingestion;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class TextChunker {

    private final int chunkSize;
    private final int overlap;

    public TextChunker(
            @Value("${helpdesk.chunking.size}") int chunkSize,
            @Value("${helpdesk.chunking.overlap}") int overlap) {
        if (chunkSize <= 0) {
            throw new IllegalArgumentException("chunk size must be positive");
        }
        if (overlap < 0 || overlap >= chunkSize) {
            throw new IllegalArgumentException("overlap must be >= 0 and smaller than the chunk size");
        }
        this.chunkSize = chunkSize;
        this.overlap = overlap;
    }

    public List<Chunk> chunk(KnowledgeDocument document) {
        String body = document.body().strip();
        if (body.isEmpty()) {
            return List.of();
        }

        String[] words = body.split("\\s+");
        int step = chunkSize - overlap;
        List<Chunk> chunks = new ArrayList<>();
        for (int start = 0; start < words.length; start += step) {
            int end = Math.min(start + chunkSize, words.length);
            String text = String.join(" ", Arrays.copyOfRange(words, start, end));
            chunks.add(new Chunk(
                    document.sourcePath(),
                    document.title(),
                    document.department(),
                    chunks.size(),
                    text));
            if (end == words.length) {
                break;
            }
        }
        return chunks;
    }
}
