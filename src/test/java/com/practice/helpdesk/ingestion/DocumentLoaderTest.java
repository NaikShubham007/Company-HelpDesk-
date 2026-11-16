package com.practice.helpdesk.ingestion;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class DocumentLoaderTest {

    @TempDir
    Path tempDir;

    @Test
    void readsFrontMatterAndBodyFromNestedFolders() throws IOException {
        write("it/vpn.md", """
                ---
                title: VPN Access
                department: IT
                owner: IT Service Desk
                last_updated: 2026-11-01
                ---

                Connect before opening internal apps.
                """);

        List<KnowledgeDocument> documents = new DocumentLoader(tempDir.toString()).load();

        assertThat(documents).hasSize(1);
        KnowledgeDocument document = documents.get(0);
        assertThat(document.sourcePath()).isEqualTo("it/vpn.md");
        assertThat(document.title()).isEqualTo("VPN Access");
        assertThat(document.department()).isEqualTo("IT");
        assertThat(document.owner()).isEqualTo("IT Service Desk");
        assertThat(document.lastUpdated()).isEqualTo("2026-11-01");
        assertThat(document.body()).isEqualTo("Connect before opening internal apps.");
    }

    @Test
    void rejectsDocumentMissingRequiredMetadata() throws IOException {
        write("hr/leave.md", """
                ---
                title: Leave
                department: HR
                ---

                Body text.
                """);

        DocumentLoader loader = new DocumentLoader(tempDir.toString());

        assertThatThrownBy(loader::load)
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("owner");
    }

    @Test
    void loadsTheSampleKnowledgeBase() {
        List<KnowledgeDocument> documents = new DocumentLoader("knowledge-base").load();

        assertThat(documents).isNotEmpty();
        assertThat(documents).allSatisfy(document -> {
            assertThat(document.title()).isNotBlank();
            assertThat(document.department()).isNotBlank();
            assertThat(document.body()).isNotBlank();
        });
    }

    private void write(String relativePath, String content) throws IOException {
        Path file = tempDir.resolve(relativePath);
        Files.createDirectories(file.getParent());
        Files.writeString(file, content);
    }
}
