package com.practice.helpdesk.retrieval;

import com.practice.helpdesk.ingestion.Chunk;

public record RetrievedChunk(Chunk chunk, double score) {
}
