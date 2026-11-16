package com.practice.helpdesk.api;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Test;

class AskRequestTest {

    @Test
    void keepsAValidQuestion() {
        assertThat(new AskRequest("How many leave days?").question()).isEqualTo("How many leave days?");
    }

    @Test
    void rejectsAMissingQuestion() {
        assertThatThrownBy(() -> new AskRequest(null)).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void rejectsABlankQuestion() {
        assertThatThrownBy(() -> new AskRequest("   ")).isInstanceOf(IllegalArgumentException.class);
    }
}
