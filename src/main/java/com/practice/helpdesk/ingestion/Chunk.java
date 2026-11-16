package com.practice.helpdesk.ingestion;

public record Chunk(
        String sourcePath,
        String title,
        String department,
        int index,
        String text) {
}
