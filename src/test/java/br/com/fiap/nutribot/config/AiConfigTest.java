package br.com.fiap.nutribot.config;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.springframework.ai.chat.messages.UserMessage;

class AiConfigTest {

    @Test
    void shouldIsolateConversationIds() {
        var memory = new AiConfig().chatMemory();
        memory.add("conversa-1", new UserMessage("Meu nome é Rafael"));
        assertEquals(1, memory.get("conversa-1").size());
        assertTrue(memory.get("conversa-2").isEmpty());
    }
}
