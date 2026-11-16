package com.practice.helpdesk.ingestion;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class DocumentLoader {

    private static final String DELIMITER = "---";
    private static final List<String> REQUIRED_KEYS = List.of("title", "department", "owner", "last_updated");

    private final Path knowledgeBaseDir;

    public DocumentLoader(@Value("${helpdesk.knowledge-base.path}") String knowledgeBasePath) {
        this.knowledgeBaseDir = Path.of(knowledgeBasePath);
    }

    public List<KnowledgeDocument> load() {
        try (Stream<Path> paths = Files.walk(knowledgeBaseDir)) {
            return paths
                    .filter(path -> Files.isRegularFile(path) && path.toString().endsWith(".md"))
                    .sorted()
                    .map(this::readDocument)
                    .toList();
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    private KnowledgeDocument readDocument(Path file) {
        String content;
        try {
            content = Files.readString(file, StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
        return parse(file, content);
    }

    private KnowledgeDocument parse(Path file, String content) {
        String[] lines = content.split("\\R", -1);
        if (lines.length == 0 || !lines[0].equals(DELIMITER)) {
            throw new IllegalStateException("Missing front matter in " + file);
        }

        int closing = -1;
        for (int i = 1; i < lines.length; i++) {
            if (lines[i].equals(DELIMITER)) {
                closing = i;
                break;
            }
        }
        if (closing < 0) {
            throw new IllegalStateException("Front matter is not closed in " + file);
        }

        Map<String, String> metadata = new HashMap<>();
        for (int i = 1; i < closing; i++) {
            int colon = lines[i].indexOf(':');
            if (colon < 0) {
                throw new IllegalStateException("Invalid front matter line in " + file + ": " + lines[i]);
            }
            metadata.put(lines[i].substring(0, colon).trim(), lines[i].substring(colon + 1).trim());
        }
        for (String key : REQUIRED_KEYS) {
            if (!metadata.containsKey(key)) {
                throw new IllegalStateException("Missing '" + key + "' in " + file);
            }
        }

        String body = String.join("\n", Arrays.copyOfRange(lines, closing + 1, lines.length)).strip();
        return new KnowledgeDocument(
                relativePath(file),
                metadata.get("title"),
                metadata.get("department"),
                metadata.get("owner"),
                metadata.get("last_updated"),
                body);
    }

    private String relativePath(Path file) {
        return knowledgeBaseDir.relativize(file).toString().replace('\\', '/');
    }
}
