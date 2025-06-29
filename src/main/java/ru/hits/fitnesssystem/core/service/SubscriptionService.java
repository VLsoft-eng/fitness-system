package ru.hits.fitnesssystem.core.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.hits.fitnesssystem.core.entity.Subscription;
import ru.hits.fitnesssystem.core.entity.User;
import ru.hits.fitnesssystem.core.exception.BadRequestException;
import ru.hits.fitnesssystem.core.exception.NotFoundException;
import ru.hits.fitnesssystem.core.repository.UserRepository;
import ru.hits.fitnesssystem.core.security.SecurityUtils;
import ru.hits.fitnesssystem.rest.model.SubscriptionDto;
import ru.hits.fitnesssystem.rest.model.*;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class SubscriptionService {

    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public SubscriptionDto getMySubscription() {
        String currentUsername = SecurityUtils.getCurrentUsername();
        if (currentUsername == null) {
            throw new BadRequestException("Пользователь не авторизован.");
        }

        User user = userRepository.findUserByUsername(currentUsername)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден."));

        Subscription subscription = user.getSubscription();
        if (subscription == null) {
            throw new NotFoundException("У пользователя нет подписки.");
        }

        return SubscriptionDto.fromEntity(subscription);
    }

    @Transactional
    public void extendSubscription(SubscriptionExtensionRequest request) {
        if (request.days() <= 0) {
            throw new BadRequestException("Количество дней должно быть положительным.");
        }

        User user = getCurrentUserWithSubscription();

        Subscription subscription = user.getSubscription();
        if (subscription == null) {
            throw new BadRequestException("У вас нет абонемента. Обратитесь к администратору");
        }
        LocalDate today = LocalDate.now();

        if (subscription.getEndDate() == null || subscription.getEndDate().isBefore(today)) {
            subscription.setStartDate(today);
            subscription.setEndDate(today.plusDays(request.days()));
        } else {
            subscription.setEndDate(subscription.getEndDate().plusDays(request.days()));
        }

        subscription.setIsActive(true);
        userRepository.save(user);
    }

    @Transactional
    public void buyPersonalTrainings(PersonalTrainingPurchaseRequest request) {
        if (request.count() <= 0) {
            throw new BadRequestException("Количество тренировок должно быть положительным.");
        }

        User user = getCurrentUserWithSubscription();

        Subscription subscription = user.getSubscription();
        if (subscription == null) {
            throw new BadRequestException("У вас нет абонемента. Обратитесь к администратору");
        }
        if (!Boolean.TRUE.equals(subscription.getIsActive())) {
            throw new BadRequestException("Нельзя покупать тренировки при неактивной подписке.");
        }

        long currentCount = subscription.getPersonalTrainingCount() == null ? 0L : subscription.getPersonalTrainingCount();
        subscription.setPersonalTrainingCount(currentCount + request.count());
        userRepository.save(user);
    }

    private User getCurrentUserWithSubscription() {
        String currentUsername = SecurityUtils.getCurrentUsername();
        if (currentUsername == null) {
            throw new BadRequestException("Пользователь не авторизован.");
        }

        User user = userRepository.findUserByUsername(currentUsername)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден."));

        Subscription subscription = user.getSubscription();
        if (subscription == null) {
            throw new NotFoundException("У пользователя нет подписки.");
        }

        return user;
    }
}

