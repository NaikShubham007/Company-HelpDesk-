package com.practice.helpdesk.retrieval;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIf;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@EnabledIf("ollamaIsRunning")
class EmbeddingEvaluationTest {

    @Autowired
    private EmbeddingModel embeddingModel;

    @Test
    void printsEmbeddingHitRates() {
        EmbeddingRetriever retriever = new EmbeddingRetriever(embeddingModel, SampleKnowledgeBase.chunks());

        RetrievalEval.report("embedding wording",
                question -> retriever.search(question, RetrievalEval.TOP_K), RetrievalEval.WORDING_CASES);
        RetrievalEval.report("embedding paraphrase",
                question -> retriever.search(question, RetrievalEval.TOP_K), RetrievalEval.PARAPHRASE_CASES);
    }

    static boolean ollamaIsRunning() {
        HttpRequest request = HttpRequest.newBuilder(URI.create("http://localhost:11434/api/tags"))
                .timeout(Duration.ofSeconds(2))
                .build();
        try {
            HttpResponse<Void> response = HttpClient.newHttpClient()
                    .send(request, HttpResponse.BodyHandlers.discarding());
            return response.statusCode() == 200;
        } catch (IOException e) {
            return false;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return false;
        }
    }
}
