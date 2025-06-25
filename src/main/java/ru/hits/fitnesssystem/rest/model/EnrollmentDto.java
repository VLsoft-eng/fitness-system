package ru.hits.fitnesssystem.rest.model;

import ru.hits.fitnesssystem.core.entity.Enrollment;
import ru.hits.fitnesssystem.core.enumeration.EnrollmentStatus;

import java.time.LocalDateTime;

public record EnrollmentDto(
        Long id,
        Long userId,
        String username,
        Long trainingSessionId,
        String trainingSessionName,
        LocalDateTime enrollmentTime,
        EnrollmentStatus status
) {
    public static EnrollmentDto fromEntity(Enrollment enrollment) {
        return new EnrollmentDto(
                enrollment.getId(),
                enrollment.getUser().getId(),
                enrollment.getUser().getUsername(),
                enrollment.getTrainingSession().getId(),
                enrollment.getTrainingSession().getName(),
                enrollment.getEnrollmentTime(),
                enrollment.getStatus()
        );
    }
}
