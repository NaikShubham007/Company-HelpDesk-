package com.practice.helpdesk.rag;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIf;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.practice.helpdesk.OllamaSupport;

@SpringBootTest
@EnabledIf("chatModelIsReady")
class AnswerServiceIntegrationTest {

    @Autowired
    private AnswerService answerService;

    @Test
    void answersAPolicyQuestionFromRetrievedSources() {
        Answer answer = answerService.ask("How many days of annual leave do I get each year?");

        System.out.println("ANSWER: " + answer.text());
        assertThat(answer.text()).isNotBlank();
        assertThat(answer.sources())
                .extracting(source -> source.chunk().sourcePath())
                .contains("hr/leave-policy.md");
    }

    @Test
    void answersAnOutOfScopeQuestion() {
        Answer answer = answerService.ask("What is the CEO's salary?");

        System.out.println("ANSWER: " + answer.text());
        assertThat(answer.text()).isNotBlank();
    }

    static boolean chatModelIsReady() {
        return OllamaSupport.hasModel("llama3.1");
    }
}
