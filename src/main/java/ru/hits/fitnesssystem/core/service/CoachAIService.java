package ru.hits.fitnesssystem.core.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.hits.fitnesssystem.core.client.YandexGPTClient;
import ru.hits.fitnesssystem.core.exception.BadRequestException;
import ru.hits.fitnesssystem.rest.model.CoachAIRequestDto;
import ru.hits.fitnesssystem.rest.model.CoachAIResponseDto;

import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
public class CoachAIService {

    private final YandexGPTClient yandexGPTClient;

    public CompletableFuture<CoachAIResponseDto> getCoachAIAdvice(CoachAIRequestDto requestDto) {
        if (requestDto.message() == null || requestDto.message().isBlank()) {
            throw new IllegalArgumentException("Message cannot be empty");
        }
        String prompt = requestDto.message();
        return yandexGPTClient.sendRequest(prompt)
                .thenApply(CoachAIResponseDto::new)
                .exceptionally(throwable -> {
                    throw new BadRequestException("Failed to process AI coach request: " + throwable.getMessage());
                });
    }
}
