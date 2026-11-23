package com.practice.helpdesk.retrieval;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.embedding.EmbeddingModel;

import com.practice.helpdesk.ingestion.Chunk;

public class EmbeddingRetriever {

    private static final Logger log = LoggerFactory.getLogger(EmbeddingRetriever.class);

    private final EmbeddingModel embeddingModel;
    private final List<Chunk> chunks;
    private final List<float[]> chunkVectors;

    public EmbeddingRetriever(EmbeddingModel embeddingModel, List<Chunk> chunks) {
        this.embeddingModel = embeddingModel;
        this.chunks = List.copyOf(chunks);
        this.chunkVectors = this.chunks.isEmpty()
                ? List.of()
                : buildIndex(embeddingModel, this.chunks);
    }

    private static List<float[]> buildIndex(EmbeddingModel embeddingModel, List<Chunk> chunks) {
        log.info("Building embedding index for {} chunks (happens once, on the first question)", chunks.size());
        long start = System.nanoTime();
        List<float[]> vectors = embeddingModel.embed(chunks.stream().map(Chunk::text).toList());
        log.info("Embedding index ready in {} ms", (System.nanoTime() - start) / 1_000_000);
        return vectors;
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
