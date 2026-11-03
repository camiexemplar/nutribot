package br.com.fiap.nutribot.service;

import br.com.fiap.nutribot.ai.Prompts;
import br.com.fiap.nutribot.dto.MealAnalysisResponse;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class MealAnalysisService {

    private static final Logger log = LoggerFactory.getLogger(MealAnalysisService.class);

    private final ChatClient client;

    public MealAnalysisService(ChatModel chatModel) {
        this.client = ChatClient.builder(chatModel).build();
    }

    public MealAnalysisResponse analyze(String description) {
        try {
            MealAnalysisResponse result = client.prompt()
                    .system(Prompts.ANALYSIS)
                    .user(description.trim())
                    .call()
                    .entity(MealAnalysisResponse.class);
            if (result == null || result.items() == null || !valid(result)) {
                throw new IllegalStateException("Invalid structured analysis");
            }
            return result;
        } catch (Exception e) {
            log.error("Falha na análise estruturada de refeição", e);
            throw new ResponseStatusException(HttpStatus.BAD_GATEWAY,
                    "Não foi possível analisar a refeição. Confira a configuração do modelo.");
        }
    }

    private static boolean valid(MealAnalysisResponse result) {
        if (!finiteNonNegative(result.totalCalories()) || !finiteNonNegative(result.protein())
                || !finiteNonNegative(result.carbs()) || !finiteNonNegative(result.fat())) {
            return false;
        }
        for (MealAnalysisResponse.Item item : result.items()) {
            if (item == null || item.food() == null || item.food().isBlank()
                    || !finiteNonNegative(item.grams()) || !finiteNonNegative(item.calories())) {
                return false;
            }
        }
        return true;
    }

    private static boolean finiteNonNegative(double value) {
        return Double.isFinite(value) && value >= 0;
    }
}
