package ru.hits.fitnesssystem.core.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.hits.fitnesssystem.core.repository.SubscriptionRepository;
import ru.hits.fitnesssystem.rest.model.SubscriptionStatisticsDto;

import java.time.LocalDate;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SubscriptionStatisticsService {
    private final SubscriptionRepository subscriptionRepository;

    @Transactional(readOnly = true)
    public SubscriptionStatisticsDto getSubscriptionStatistics() {
        LocalDate today = LocalDate.now();
        LocalDate oneMonthLater = today.plusMonths(1);

        Long totalActiveSubscriptions = subscriptionRepository.countByIsActive(true);
        Long expiringSubscriptionsNextMonth = subscriptionRepository.countByEndDateBetween(today, oneMonthLater);

        Map<String, Long> subscriptionsStartDateDistribution = subscriptionRepository.countSubscriptionsByStartDateMonth().stream()
                .collect(Collectors.toMap(
                        arr -> Objects.toString(arr[0], "Unknown Month (Start Date)"),
                        arr -> (Long) arr[1]
                ));

        Map<String, Long> subscriptionsEndDateDistribution = subscriptionRepository.countSubscriptionsByEndDateMonth().stream()
                .collect(Collectors.toMap(
                        arr -> Objects.toString(arr[0], "Unknown Month (End Date)"),
                        arr -> (Long) arr[1]
                ));

        return SubscriptionStatisticsDto.builder()
                .totalActiveSubscriptions(totalActiveSubscriptions)
                .expiringSubscriptionsNextMonth(expiringSubscriptionsNextMonth)
                .subscriptionsStartDateDistribution(subscriptionsStartDateDistribution)
                .subscriptionsEndDateDistribution(subscriptionsEndDateDistribution)
                .build();
    }
}
