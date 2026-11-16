package com.practice.helpdesk.retrieval;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.jupiter.api.Test;

class SampleKnowledgeBaseRetrievalTest {

    private final KeywordRetriever retriever = SampleKnowledgeBase.keywordRetriever();

    @Test
    void findsTheLeavePolicyForCarryForwardQuestion() {
        List<RetrievedChunk> results = retriever.search("unused leave forward", 3);

        assertThat(results).isNotEmpty();
        assertThat(results.get(0).chunk().sourcePath()).isEqualTo("hr/leave-policy.md");
    }

    @Test
    void findsTheVpnPolicyForVpnQuestion() {
        List<RetrievedChunk> results = retriever.search("VPN install", 3);

        assertThat(results).isNotEmpty();
        assertThat(results.get(0).chunk().sourcePath()).isEqualTo("it/vpn-access.md");
    }
}
