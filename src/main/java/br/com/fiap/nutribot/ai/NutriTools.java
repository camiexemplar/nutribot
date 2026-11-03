package br.com.fiap.nutribot.ai;

import br.com.fiap.nutribot.dto.MealEntryResponse;
import br.com.fiap.nutribot.dto.SummaryResponse;
import br.com.fiap.nutribot.model.Food;
import br.com.fiap.nutribot.model.MealType;
import br.com.fiap.nutribot.service.FoodService;
import br.com.fiap.nutribot.service.MealService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class NutriTools {

    private static final Logger log = LoggerFactory.getLogger(NutriTools.class);

    private final FoodService foodService;
    private final MealService mealService;

    @Tool(description = "Busca alimentos na tabela nutricional da clínica. Os valores de calorias, proteínas, carboidratos e gorduras são por 100 gramas. Use sempre que o usuário perguntar valores de alimentos.")
    public List<Food> searchFood(@ToolParam(description = "Nome de um alimento, por exemplo tapioca ou ovo") String name) {
        log.info("Tool searchFood({})", name);
        return foodService.search(name).stream().limit(15).toList();
    }

    @Tool(description = "Consulta o resumo alimentar de hoje, incluindo calorias consumidas, meta, calorias restantes e macronutrientes. Use sempre para perguntas sobre o diário ou o saldo de calorias.")
    public SummaryResponse getDailySummary() {
        log.info("Tool getDailySummary()");
        return mealService.summary(null);
    }

    @Tool(description = "Registra efetivamente no diário de hoje uma refeição informada pelo paciente. Converta a quantidade para gramas e use o nome de um alimento da tabela. Para tipo de refeição use BREAKFAST, LUNCH, DINNER ou SNACK.")
    public MealEntryResponse logMeal(
            @ToolParam(description = "Nome do alimento na tabela, por exemplo Ovo cozido") String foodName,
            @ToolParam(description = "Quantidade consumida, em gramas, maior que zero") double grams,
            @ToolParam(description = "Tipo da refeição: BREAKFAST, LUNCH, DINNER ou SNACK") MealType mealType) {
        log.info("Tool logMeal({}, {}, {})", foodName, grams, mealType);
        return MealEntryResponse.from(mealService.log(foodName, grams, mealType));
    }
}
