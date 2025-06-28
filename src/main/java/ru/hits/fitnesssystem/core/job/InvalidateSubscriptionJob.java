package ru.hits.fitnesssystem.core.job;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import ru.hits.fitnesssystem.core.entity.Subscription;
import ru.hits.fitnesssystem.core.repository.SubscriptionRepository;

import java.time.LocalDate;
import java.util.List;

@RequiredArgsConstructor
@Component
public class InvalidateSubscriptionJob {

    private final SubscriptionRepository subscriptionRepository;

    @Scheduled(fixedRate = 50000)
    public void invalidateSubscriptions() {
        List<Subscription> subscriptions = subscriptionRepository.findAll();

        LocalDate today = LocalDate.now();

        for (Subscription subscription : subscriptions) {
            LocalDate endDate = subscription.getEndDate();
            if (endDate != null && !endDate.isAfter(today)) {
                if (Boolean.TRUE.equals(subscription.getIsActive())) {
                    subscription.setIsActive(false);
                    subscriptionRepository.save(subscription);
                }
            }
        }
    }
}
