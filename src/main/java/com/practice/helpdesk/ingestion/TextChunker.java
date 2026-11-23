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
        List<Chunk> chunks = new ArrayList<>();
        for (String section : sections(document.body())) {
            for (String text : windows(section)) {
                chunks.add(new Chunk(
                        document.sourcePath(),
                        document.title(),
                        document.department(),
                        chunks.size(),
                        text));
            }
        }
        return chunks;
    }

    // A Markdown heading starts a new section, so each chunk keeps its heading with its text.
    private static List<String> sections(String body) {
        List<String> sections = new ArrayList<>();
        StringBuilder section = new StringBuilder();
        boolean hasText = false;
        for (String line : body.split("\\R")) {
            if (line.startsWith("#")) {
                if (hasText) {
                    sections.add(section.toString());
                }
                section = new StringBuilder(line.replaceFirst("^#+\\s*", ""));
                hasText = false;
            } else if (!line.isBlank()) {
                section.append(' ').append(line.strip());
                hasText = true;
            }
        }
        if (hasText) {
            sections.add(section.toString());
        }
        return sections;
    }

    // Only sections longer than the chunk size are split, so their pieces overlap.
    private List<String> windows(String section) {
        String[] words = section.strip().split("\\s+");
        int step = chunkSize - overlap;
        List<String> windows = new ArrayList<>();
        for (int start = 0; start < words.length; start += step) {
            int end = Math.min(start + chunkSize, words.length);
            windows.add(String.join(" ", Arrays.copyOfRange(words, start, end)));
            if (end == words.length) {
                break;
            }
        }
        return windows;
    }
}
