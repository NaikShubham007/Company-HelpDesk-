package com.practice.helpdesk.rag;

import java.util.List;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;

import com.practice.helpdesk.retrieval.EmbeddingRetriever;
import com.practice.helpdesk.retrieval.RetrievedChunk;

@Service
public class AnswerService {

    private static final int TOP_K = 3;

    private final EmbeddingRetriever retriever;
    private final ChatClient chatClient;

    public AnswerService(EmbeddingRetriever retriever, ChatClient.Builder chatClientBuilder) {
        this.retriever = retriever;
        this.chatClient = chatClientBuilder.build();
    }

    public Answer ask(String question) {
        List<RetrievedChunk> sources = retriever.search(question, TOP_K);
        String text = chatClient.prompt()
                .system(PromptBuilder.SYSTEM_INSTRUCTIONS)
                .user(PromptBuilder.userMessage(question, sources))
                .call()
                .content();
        return new Answer(text, sources);
    }
}
