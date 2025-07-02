package ru.hits.fitnesssystem.rest.model;

import lombok.Builder;

import java.util.Map;

@Builder
public record SubscriptionStatisticsDto(Long totalActiveSubscriptions,
                                        Long expiringSubscriptionsNextMonth,
                                        Map<String, Long> subscriptionsStartDateDistribution,
                                        Map<String, Long> subscriptionsEndDateDistribution) {
}
