package com.practice.helpdesk.rag;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.RETURNS_DEEP_STUBS;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.ai.chat.client.ChatClient;

import com.practice.helpdesk.ingestion.Chunk;
import com.practice.helpdesk.retrieval.EmbeddingRetriever;
import com.practice.helpdesk.retrieval.RetrievedChunk;

class AnswerServiceTest {

    @Test
    void returnsTheModelTextAndTheSourcesItUsed() {
        List<RetrievedChunk> sources = List.of(new RetrievedChunk(
                new Chunk("it/vpn-access.md", "VPN Access", "IT", 0, "Connect the VPN first."), 0.9));
        EmbeddingRetriever retriever = mock(EmbeddingRetriever.class);
        when(retriever.search("How do I connect?", 3)).thenReturn(sources);

        ChatClient chatClient = mock(ChatClient.class, RETURNS_DEEP_STUBS);
        when(chatClient.prompt().system(anyString()).user(anyString()).call().content())
                .thenReturn("Connect the VPN first [1].");
        ChatClient.Builder builder = mock(ChatClient.Builder.class);
        when(builder.build()).thenReturn(chatClient);

        Answer answer = new AnswerService(retriever, builder).ask("How do I connect?");

        assertThat(answer.text()).isEqualTo("Connect the VPN first [1].");
        assertThat(answer.sources()).isEqualTo(sources);
    }
}
