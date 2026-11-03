package br.com.fiap.nutribot.dto;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;

import java.util.List;
import org.junit.jupiter.api.Test;

class ContractTest {

    @Test
    void shouldKeepChatContract() {
        assertEquals("Oi", new ChatResponse("Oi").answer());
        assertEquals("id", new ChatRequest("id", "Oi").conversationId());
    }

    @Test
    void shouldKeepNumericAnalysisContract() {
        var item = new MealAnalysisResponse.Item("Arroz", 150.0, 192.0);
        var response = new MealAnalysisResponse(List.of(item), 192.0, 3.0, 42.0, 0.2);
        assertInstanceOf(Double.class, response.totalCalories());
        assertEquals(150.0, response.items().getFirst().grams());
    }
}
