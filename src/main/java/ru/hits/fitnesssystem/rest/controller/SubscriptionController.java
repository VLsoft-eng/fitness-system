package ru.hits.fitnesssystem.rest.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.hits.fitnesssystem.core.service.SubscriptionService;
import ru.hits.fitnesssystem.rest.model.PersonalTrainingPurchaseRequest;
import ru.hits.fitnesssystem.rest.model.SubscriptionDto;
import ru.hits.fitnesssystem.rest.model.SubscriptionExtensionRequest;

@RequiredArgsConstructor
@RestController
@RequestMapping("/subscription")
public class SubscriptionController {

    private final SubscriptionService subscriptionService;

    @GetMapping("/my")
    public SubscriptionDto getMySubscription() {
        return subscriptionService.getMySubscription();
    }

    @PostMapping("/extend")
    public void extendSubscription(@RequestBody SubscriptionExtensionRequest request) {
        subscriptionService.extendSubscription(request);
    }

    @PostMapping("/buy-trainings")
    public void buyPersonalTrainings(@RequestBody PersonalTrainingPurchaseRequest request) {
        subscriptionService.buyPersonalTrainings(request);
    }

    @PostMapping("/assign-specific/{subscriptionSpecificId}")
    public void assignSpecificTrainings(@PathVariable Long subscriptionSpecificId) {
        subscriptionService.assignSpecificSubscription(subscriptionSpecificId);
    }
}

