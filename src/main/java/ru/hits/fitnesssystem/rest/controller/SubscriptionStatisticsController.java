package ru.hits.fitnesssystem.rest.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.hits.fitnesssystem.core.service.SubscriptionStatisticsService;
import ru.hits.fitnesssystem.rest.model.SubscriptionStatisticsDto;

@RestController
@RequestMapping("/statistics/subscriptions")
@RequiredArgsConstructor
public class SubscriptionStatisticsController {
    private final SubscriptionStatisticsService subscriptionStatisticsService;

    @GetMapping("/overview")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<SubscriptionStatisticsDto> getSubscriptionStatistics() {
        SubscriptionStatisticsDto statistics = subscriptionStatisticsService.getSubscriptionStatistics();
        return ResponseEntity.ok(statistics);
    }
}
