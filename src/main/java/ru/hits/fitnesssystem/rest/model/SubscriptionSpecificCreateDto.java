package ru.hits.fitnesssystem.rest.model;

public record SubscriptionSpecificCreateDto(
        String name,
        String description,
        Long personalTrainingCount,
        Long subscriptionDaysCount
) {}