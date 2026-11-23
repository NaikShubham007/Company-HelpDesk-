package com.practice.helpdesk.rag;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Value;
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
    private final String chatModel;

    public AnswerService(
            @Lazy EmbeddingRetriever retriever,
            ChatClient.Builder chatClientBuilder,
            @Value("${spring.ai.ollama.chat.options.model}") String chatModel) {
        this.retriever = retriever;
        this.chatClient = chatClientBuilder.build();
        this.chatModel = chatModel;
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

        String systemMessage = PromptBuilder.SYSTEM_INSTRUCTIONS;
        String userMessage = PromptBuilder.userMessage(question, sources);
        log.info("Ollama chat call: model={}, system message {} characters, user message {} characters",
                chatModel, systemMessage.length(), userMessage.length());

        long chatStart = System.nanoTime();
        String text;
        try {
            text = chatClient.prompt()
                    .system(systemMessage)
                    .user(userMessage)
                    .call()
                    .content();
        } catch (RuntimeException e) {
            log.error("Ollama chat call failed for model {}: {}. Check that the model appears in 'ollama list'.",
                    chatModel, e.getMessage());
            throw e;
        }
        log.info("Ollama chat call finished in {} ms: {}", (System.nanoTime() - chatStart) / 1_000_000, text);
        return new Answer(text, sources);
    }
}
