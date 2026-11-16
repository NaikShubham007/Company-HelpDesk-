package com.practice.helpdesk.retrieval;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;

import java.util.List;
import java.util.Locale;

import org.junit.jupiter.api.Test;
import org.springframework.ai.document.Document;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.embedding.EmbeddingRequest;
import org.springframework.ai.embedding.EmbeddingResponse;

import com.practice.helpdesk.ingestion.Chunk;

class EmbeddingRetrieverTest {

    @Test
    void ranksTheChunkWithTheSameWordsFirst() {
        EmbeddingRetriever retriever = new EmbeddingRetriever(
                new WordCountEmbeddingModel(List.of("vpn", "password", "leave")),
                List.of(chunk("it/vpn.md", "VPN password reset"), chunk("hr/leave.md", "annual leave days")));

        List<RetrievedChunk> results = retriever.search("vpn", 2);

        assertThat(results.get(0).chunk().sourcePath()).isEqualTo("it/vpn.md");
    }

    @Test
    void cosineSimilarityIsOneForSameDirection() {
        assertThat(EmbeddingRetriever.cosineSimilarity(new float[] {1, 0}, new float[] {3, 0}))
                .isCloseTo(1.0, within(1e-9));
    }

    @Test
    void cosineSimilarityIsZeroForUnrelatedDirections() {
        assertThat(EmbeddingRetriever.cosineSimilarity(new float[] {1, 0}, new float[] {0, 1}))
                .isCloseTo(0.0, within(1e-9));
    }

    @Test
    void returnsNothingForAnEmptyIndex() {
        EmbeddingRetriever retriever = new EmbeddingRetriever(
                new WordCountEmbeddingModel(List.of("vpn")), List.of());

        assertThat(retriever.search("vpn", 3)).isEmpty();
    }

    private static Chunk chunk(String sourcePath, String text) {
        return new Chunk(sourcePath, "Test", "IT", 0, text);
    }

    /** Counts how often each vocabulary word appears, so tests can predict the vectors. */
    private static final class WordCountEmbeddingModel implements EmbeddingModel {

        private final List<String> vocabulary;

        WordCountEmbeddingModel(List<String> vocabulary) {
            this.vocabulary = vocabulary;
        }

        @Override
        public float[] embed(String text) {
            String lower = text.toLowerCase(Locale.ROOT);
            float[] vector = new float[vocabulary.size()];
            for (int i = 0; i < vocabulary.size(); i++) {
                vector[i] = lower.contains(vocabulary.get(i)) ? 1 : 0;
            }
            return vector;
        }

        @Override
        public List<float[]> embed(List<String> texts) {
            return texts.stream().map(this::embed).toList();
        }

        @Override
        public EmbeddingResponse call(EmbeddingRequest request) {
            throw new UnsupportedOperationException("not used in tests");
        }

        @Override
        public float[] embed(Document document) {
            throw new UnsupportedOperationException("not used in tests");
        }
    }
}
