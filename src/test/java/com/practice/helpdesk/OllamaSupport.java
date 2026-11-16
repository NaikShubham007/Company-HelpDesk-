package com.practice.helpdesk;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

public final class OllamaSupport {

    private static final URI TAGS_URI = URI.create("http://localhost:11434/api/tags");

    private OllamaSupport() {
    }

    public static boolean isRunning() {
        return tagsBody() != null;
    }

    public static boolean hasModel(String model) {
        String body = tagsBody();
        return body != null && body.contains("\"name\":\"" + model);
    }

    private static String tagsBody() {
        HttpRequest request = HttpRequest.newBuilder(TAGS_URI)
                .timeout(Duration.ofSeconds(2))
                .build();
        try {
            HttpResponse<String> response = HttpClient.newHttpClient()
                    .send(request, HttpResponse.BodyHandlers.ofString());
            return response.statusCode() == 200 ? response.body() : null;
        } catch (IOException e) {
            return null;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return null;
        }
    }
}
