package br.com.fiap.nutribot.dto;

import java.time.LocalDate;

import br.com.fiap.nutribot.model.MealType;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record MealEntryRequest(
        LocalDate date,
        @NotNull(message = "mealType is required") MealType mealType,
        @NotNull(message = "foodId is required") Long foodId,
        @NotNull(message = "grams is required") @Positive(message = "grams must be positive") Double grams
) {}
