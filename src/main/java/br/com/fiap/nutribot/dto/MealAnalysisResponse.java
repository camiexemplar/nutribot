package br.com.fiap.nutribot.dto;

import java.util.List;

public record MealAnalysisResponse(
        List<Item> items,
        double totalCalories,
        double protein,
        double carbs,
        double fat
) {
    public record Item(String food, double grams, double calories) {
    }
}
