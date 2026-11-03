package br.com.fiap.nutribot.controller;

import br.com.fiap.nutribot.dto.ChatRequest;
import br.com.fiap.nutribot.dto.ChatResponse;
import br.com.fiap.nutribot.service.ChatService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class ChatController {

    private final ChatService service;

    @PostMapping("/chat")
    public ChatResponse chat(@RequestBody @Valid ChatRequest request) {
        return service.chat(request);
    }
}
