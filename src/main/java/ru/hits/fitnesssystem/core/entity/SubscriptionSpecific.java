package ru.hits.fitnesssystem.core.entity;

import jakarta.persistence.*;
import lombok.*;

@Table(name = "subscription_specific")
@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SubscriptionSpecific {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name")
    private String name;

    @Column(name = "description")
    private String description;

    @Builder.Default
    @Column(nullable = false, name = "personal_training_count")
    private Long personalTrainingCount = 0L;

    @Builder.Default
    @Column(nullable = false, name = "subscription_days_count")
    private Long subscriptionDaysCount = 0L;
}
