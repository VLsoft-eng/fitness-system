package ru.hits.fitnesssystem.rest.model;

public record SubscriptionSpecificResponseDto(
        Long id,
        String name,
        String description,
        Long personalTrainingCount,
        Long subscriptionDaysCount
) {}