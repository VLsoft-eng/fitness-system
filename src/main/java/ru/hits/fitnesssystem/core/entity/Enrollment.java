package ru.hits.fitnesssystem.core.entity;

import jakarta.persistence.*;
import lombok.*;
import ru.hits.fitnesssystem.core.enumeration.EnrollmentCallType;
import ru.hits.fitnesssystem.core.enumeration.EnrollmentStatus;

import java.time.LocalDateTime;

@Builder
@RequiredArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "enrollments", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"user_id", "training_session_id"})
})
public class Enrollment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "training_session_id", nullable = false)
    private TrainingSession trainingSession;

    @Column(name = "enrollment_time", nullable = false, updatable = false)
    private LocalDateTime enrollmentTime;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private EnrollmentStatus status;

    @Enumerated(EnumType.STRING)
    @Column(name = "enrollment_call_type")
    private EnrollmentCallType enrollmentCallType;

    @PrePersist
    protected void onCreate() {
        this.enrollmentTime = LocalDateTime.now();
    }
}