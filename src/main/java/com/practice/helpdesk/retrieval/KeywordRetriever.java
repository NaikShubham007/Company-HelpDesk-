package com.practice.helpdesk.retrieval;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import com.practice.helpdesk.ingestion.Chunk;

public class KeywordRetriever {

    private static final Set<String> STOP_WORDS = Set.of(
            "a", "an", "and", "are", "at", "be", "by", "can", "do", "for", "from",
            "how", "i", "if", "in", "is", "it", "my", "of", "on", "or", "our",
            "the", "to", "what", "when", "with", "you", "your");

    private final List<Chunk> chunks;
    private final List<Map<String, Integer>> termCounts = new ArrayList<>();
    private final Map<String, Integer> documentFrequency = new HashMap<>();

    public KeywordRetriever(List<Chunk> chunks) {
        this.chunks = List.copyOf(chunks);
        for (Chunk chunk : this.chunks) {
            Map<String, Integer> counts = new HashMap<>();
            for (String term : tokenize(chunk.text())) {
                counts.merge(term, 1, Integer::sum);
            }
            termCounts.add(counts);
            for (String term : counts.keySet()) {
                documentFrequency.merge(term, 1, Integer::sum);
            }
        }
    }

    public List<RetrievedChunk> search(String question, int topK) {
        Set<String> queryTerms = Set.copyOf(tokenize(question));
        List<RetrievedChunk> results = new ArrayList<>();
        for (int i = 0; i < chunks.size(); i++) {
            Map<String, Integer> counts = termCounts.get(i);
            double score = 0;
            for (String term : queryTerms) {
                int termFrequency = counts.getOrDefault(term, 0);
                if (termFrequency > 0) {
                    score += termFrequency * inverseDocumentFrequency(term);
                }
            }
            if (score > 0) {
                results.add(new RetrievedChunk(chunks.get(i), score));
            }
        }
        results.sort(Comparator.comparingDouble(RetrievedChunk::score).reversed());
        return results.subList(0, Math.min(topK, results.size()));
    }

    private double inverseDocumentFrequency(String term) {
        int documents = documentFrequency.getOrDefault(term, 0);
        return Math.log((double) chunks.size() / documents) + 1;
    }

    private List<String> tokenize(String text) {
        List<String> terms = new ArrayList<>();
        for (String word : text.toLowerCase(Locale.ROOT).split("[^a-z0-9]+")) {
            if (!word.isEmpty() && !STOP_WORDS.contains(word)) {
                terms.add(word);
            }
        }
        return terms;
    }
}
