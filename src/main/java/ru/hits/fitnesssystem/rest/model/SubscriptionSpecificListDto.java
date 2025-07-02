package ru.hits.fitnesssystem.rest.model;

import java.util.List;

public record SubscriptionSpecificListDto(
        List<SubscriptionSpecificResponseDto> subscriptions
) {}