package ru.hits.fitnesssystem.core.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Table(name = "subscription")
@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Subscription {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "subscriber_id")
    private User subscriber;

    @Builder.Default
    @Column(nullable = false, name = "personal_training_count")
    private Long personalTrainingCount = 0L;

    @Builder.Default
    @Column(nullable = false, name = "is_active")
    private Boolean isActive = false;

    @Column(name = "start_date")
    private LocalDate startDate;

    @Column(name = "end_date")
    private LocalDate endDate;
}

