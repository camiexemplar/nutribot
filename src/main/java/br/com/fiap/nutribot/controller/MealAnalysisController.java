package br.com.fiap.nutribot.controller;

import br.com.fiap.nutribot.dto.MealAnalysisRequest;
import br.com.fiap.nutribot.dto.MealAnalysisResponse;
import br.com.fiap.nutribot.service.MealAnalysisService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class MealAnalysisController {

    private final MealAnalysisService service;

    @PostMapping("/meals/analyze")
    public MealAnalysisResponse analyze(@RequestBody @Valid MealAnalysisRequest request) {
        return service.analyze(request.description());
    }
}
