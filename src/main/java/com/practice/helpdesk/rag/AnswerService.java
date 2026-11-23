package com.practice.helpdesk.rag;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import com.practice.helpdesk.retrieval.EmbeddingRetriever;
import com.practice.helpdesk.retrieval.RetrievedChunk;

@Service
public class AnswerService {

    private static final Logger log = LoggerFactory.getLogger(AnswerService.class);
    private static final int TOP_K = 3;

    private final EmbeddingRetriever retriever;
    private final ChatClient chatClient;

    public AnswerService(@Lazy EmbeddingRetriever retriever, ChatClient.Builder chatClientBuilder) {
        this.retriever = retriever;
        this.chatClient = chatClientBuilder.build();
    }

    public Answer ask(String question) {
        log.info("Question received: \"{}\"", question);

        long retrievalStart = System.nanoTime();
        List<RetrievedChunk> sources = retriever.search(question, TOP_K);
        log.info("Retrieved {} passages in {} ms", sources.size(), (System.nanoTime() - retrievalStart) / 1_000_000);
        for (int i = 0; i < sources.size(); i++) {
            RetrievedChunk source = sources.get(i);
            log.info("  [{}] {} score={}", i + 1, source.chunk().sourcePath(), "%.3f".formatted(source.score()));
        }

        log.info("Sending the question and passages to the chat model");
        long chatStart = System.nanoTime();
        String text = chatClient.prompt()
                .system(PromptBuilder.SYSTEM_INSTRUCTIONS)
                .user(PromptBuilder.userMessage(question, sources))
                .call()
                .content();
        log.info("Answer received in {} ms: {}", (System.nanoTime() - chatStart) / 1_000_000, text);
        return new Answer(text, sources);
    }
}
