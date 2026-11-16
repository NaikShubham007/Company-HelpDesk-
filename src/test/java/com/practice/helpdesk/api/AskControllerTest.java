package com.practice.helpdesk.api;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.practice.helpdesk.ingestion.Chunk;
import com.practice.helpdesk.rag.Answer;
import com.practice.helpdesk.rag.AnswerService;
import com.practice.helpdesk.retrieval.RetrievedChunk;

@WebMvcTest(AskController.class)
class AskControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AnswerService answerService;

    @Test
    void returnsTheAnswerAndItsSources() throws Exception {
        Chunk chunk = new Chunk("hr/leave-policy.md", "Annual Leave", "HR", 0, "Full-time staff get 20 days.");
        when(answerService.ask("How many days of leave?"))
                .thenReturn(new Answer("You get 20 days [1].", List.of(new RetrievedChunk(chunk, 0.8))));

        mockMvc.perform(post("/api/ask")
                        .contentType("application/json")
                        .content("{\"question\": \"How many days of leave?\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.answer").value("You get 20 days [1]."))
                .andExpect(jsonPath("$.sources[0].path").value("hr/leave-policy.md"))
                .andExpect(jsonPath("$.sources[0].title").value("Annual Leave"));
    }

    @Test
    void rejectsABlankQuestionWithoutCallingTheService() throws Exception {
        mockMvc.perform(post("/api/ask")
                        .contentType("application/json")
                        .content("{\"question\": \"   \"}"))
                .andExpect(status().isBadRequest());
    }
}
