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
@RequiredArgsConstructor
public class Subscription {
    @Id
    private Long id;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "subscriber_id", nullable = false)
    private User subscriber;

    @Column(nullable = false, name = "personal_training_count")
    private Long personalTrainingCount = 0L;

    @Column(nullable = false, name = "is_active")
    private Boolean isActive = false;

    @Column(name = "start_date")
    private LocalDate startDate;

    @Column(name = "end_date")
    private LocalDate endDate;
}
