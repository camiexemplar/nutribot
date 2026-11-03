package br.com.fiap.nutribot.ai;

import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

class PromptsTest {

    @Test
    void shouldDescribeClinicPersona() {
        assertTrue(Prompts.SYSTEM.contains("Clínica Equilíbrio"));
        assertTrue(Prompts.SYSTEM.contains("português"));
        assertTrue(Prompts.SYSTEM.contains("diagnósticos"));
    }

    @Test
    void shouldRequestAllTools() {
        assertTrue(Prompts.SYSTEM.contains("searchFood"));
        assertTrue(Prompts.SYSTEM.contains("getDailySummary"));
        assertTrue(Prompts.SYSTEM.contains("logMeal"));
    }

    @Test
    void shouldContainRequiredRagPlaceholders() {
        assertTrue(Prompts.RAG.contains("{query}"));
        assertTrue(Prompts.RAG.contains("{question_answer_context}"));
    }

    @Test
    void shouldNotPersistAnalysis() {
        assertTrue(Prompts.ANALYSIS.contains("não grava"));
    }
}
