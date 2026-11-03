package br.com.fiap.nutribot.service;

import br.com.fiap.nutribot.ai.NutriTools;
import br.com.fiap.nutribot.ai.Prompts;
import br.com.fiap.nutribot.dto.ChatRequest;
import br.com.fiap.nutribot.dto.ChatResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.vectorstore.QuestionAnswerAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.core.Ordered;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class ChatService {

    private static final Logger log = LoggerFactory.getLogger(ChatService.class);

    private final ChatClient client;

    public ChatService(ChatClient.Builder builder, ChatMemory memory,
                       VectorStore vectorStore, NutriTools tools) {
        var ragTemplate = PromptTemplate.builder().template(Prompts.RAG).build();
        this.client = builder
                .defaultSystem(Prompts.SYSTEM)
                .defaultAdvisors(
                        MessageChatMemoryAdvisor.builder(memory).build(),
                        QuestionAnswerAdvisor.builder(vectorStore)
                                .promptTemplate(ragTemplate)
                                .order(Ordered.HIGHEST_PRECEDENCE + 250)
                                .build())
                .defaultTools(tools)
                .build();
    }

    public ChatResponse chat(ChatRequest request) {
        try {
            String answer = client.prompt()
                    .user(request.message().trim())
                    .advisors(advisor -> advisor.param(ChatMemory.CONVERSATION_ID,
                            request.conversationId().trim()))
                    .call()
                    .content();
            if (answer == null || answer.isBlank()) {
                throw new IllegalStateException("Empty AI answer");
            }
            return new ChatResponse(answer);
        } catch (Exception e) {
            log.error("Falha na chamada de chat ao modelo de IA", e);
            throw new ResponseStatusException(HttpStatus.BAD_GATEWAY,
                    "Não foi possível obter uma resposta da IA. Confira a configuração do modelo.");
        }
    }
}
