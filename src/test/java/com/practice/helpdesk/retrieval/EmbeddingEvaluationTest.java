package com.practice.helpdesk.retrieval;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIf;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.practice.helpdesk.OllamaSupport;

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
        return OllamaSupport.isRunning();
    }
}
