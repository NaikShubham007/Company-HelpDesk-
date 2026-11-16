package com.practice.helpdesk.rag;

import java.util.List;

import com.practice.helpdesk.retrieval.RetrievedChunk;

public record Answer(String text, List<RetrievedChunk> sources) {
}
