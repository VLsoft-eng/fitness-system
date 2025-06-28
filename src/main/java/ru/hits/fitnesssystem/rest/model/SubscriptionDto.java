package ru.hits.fitnesssystem.rest.model;

import ru.hits.fitnesssystem.core.entity.Subscription;

import java.time.LocalDate;

public record SubscriptionDto(
        Long id,
        Long personalTrainingCount,
        Boolean isActive,
        LocalDate startDate,
        LocalDate endDate
) {
    public static SubscriptionDto fromEntity(Subscription subscription) {
        return new SubscriptionDto(
                subscription.getId(),
                subscription.getPersonalTrainingCount(),
                subscription.getIsActive(),
                subscription.getStartDate(),
                subscription.getEndDate()
        );
    }
}
