package ru.hits.fitnesssystem.rest.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.hits.fitnesssystem.core.service.CoachAIService;
import ru.hits.fitnesssystem.rest.model.CoachAIRequestDto;
import ru.hits.fitnesssystem.rest.model.CoachAIResponseDto;

import java.util.concurrent.CompletableFuture;

@RequiredArgsConstructor
@RequestMapping("/ai-trainer")
@RestController("/ai-trainer")
public class YandexController {

    private final CoachAIService coachAIService;

    @PostMapping
    public CompletableFuture<ResponseEntity<CoachAIResponseDto>> getCoachAIAdvice(@RequestBody CoachAIRequestDto requestDto) {
        return coachAIService.getCoachAIAdvice(requestDto)
                .thenApply(ResponseEntity::ok);
    }
}
