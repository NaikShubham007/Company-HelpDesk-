package com.practice.helpdesk.ingestion;

public record KnowledgeDocument(
        String sourcePath,
        String title,
        String department,
        String owner,
        String lastUpdated,
        String body) {
}
