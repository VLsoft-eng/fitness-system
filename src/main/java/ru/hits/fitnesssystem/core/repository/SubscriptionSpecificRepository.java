package ru.hits.fitnesssystem.core.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.hits.fitnesssystem.core.entity.SubscriptionSpecific;

public interface SubscriptionSpecificRepository extends JpaRepository<SubscriptionSpecific, Long> {
}