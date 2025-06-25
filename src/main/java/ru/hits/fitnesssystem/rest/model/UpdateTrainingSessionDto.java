package ru.hits.fitnesssystem.rest.model;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.Min;
import ru.hits.fitnesssystem.core.enumeration.TrainingSessionType;

import java.time.LocalDateTime;

public record UpdateTrainingSessionDto(
        String name,

        String description,

        TrainingSessionType type,

        Long trainerId,

        @FutureOrPresent(message = "Время начала занятия должно быть в будущем или настоящем")
        LocalDateTime startTime,

        @Min(value = 1, message = "Продолжительность занятия должна быть не менее 1 минуты")
        Integer durationMinutes,

        @Min(value = 1, message = "Максимальное количество участников должно быть не менее 1")
        Integer maxParticipants,

        String location
) {
}
