package com.practice.helpdesk.api;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.practice.helpdesk.rag.AnswerService;

@WebMvcTest(AskController.class)
class ChatPageTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AnswerService answerService;

    @Test
    void servesTheChatPageThatPostsToTheAskEndpoint() throws Exception {
        mockMvc.perform(get("/index.html"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("id=\"ask-form\"")))
                .andExpect(content().string(containsString("fetch('/api/ask'")));
    }
}
