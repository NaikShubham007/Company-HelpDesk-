package com.practice.helpdesk.api;

public record AskRequest(String question) {

    public AskRequest {
        if (question == null || question.isBlank()) {
            throw new IllegalArgumentException("question must not be blank");
        }
    }
}
