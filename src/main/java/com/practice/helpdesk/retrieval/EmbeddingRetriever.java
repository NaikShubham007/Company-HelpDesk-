package com.practice.helpdesk.retrieval;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import org.springframework.ai.embedding.EmbeddingModel;

import com.practice.helpdesk.ingestion.Chunk;

public class EmbeddingRetriever {

    private final EmbeddingModel embeddingModel;
    private final List<Chunk> chunks;
    private final List<float[]> chunkVectors;

    public EmbeddingRetriever(EmbeddingModel embeddingModel, List<Chunk> chunks) {
        this.embeddingModel = embeddingModel;
        this.chunks = List.copyOf(chunks);
        this.chunkVectors = this.chunks.isEmpty()
                ? List.of()
                : embeddingModel.embed(this.chunks.stream().map(Chunk::text).toList());
    }

    public List<RetrievedChunk> search(String question, int topK) {
        float[] questionVector = embeddingModel.embed(question);
        List<RetrievedChunk> results = new ArrayList<>();
        for (int i = 0; i < chunks.size(); i++) {
            double score = cosineSimilarity(questionVector, chunkVectors.get(i));
            results.add(new RetrievedChunk(chunks.get(i), score));
        }
        results.sort(Comparator.comparingDouble(RetrievedChunk::score).reversed());
        return results.subList(0, Math.min(topK, results.size()));
    }

    static double cosineSimilarity(float[] a, float[] b) {
        double dot = 0;
        double normA = 0;
        double normB = 0;
        for (int i = 0; i < a.length; i++) {
            dot += a[i] * b[i];
            normA += a[i] * a[i];
            normB += b[i] * b[i];
        }
        double denominator = Math.sqrt(normA) * Math.sqrt(normB);
        return denominator == 0 ? 0 : dot / denominator;
    }
}
