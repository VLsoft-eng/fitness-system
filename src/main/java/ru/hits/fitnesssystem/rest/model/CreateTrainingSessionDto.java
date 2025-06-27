package ru.hits.fitnesssystem.rest.model;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import ru.hits.fitnesssystem.core.enumeration.TrainingSessionType;

import java.time.LocalDateTime;

public record CreateTrainingSessionDto(
        @NotBlank(message = "Название занятия не может быть пустым") String name,
        String description,

        @NotNull(message = "Тип занятия не может быть пустым") TrainingSessionType type,
        Long trainerId,

        @NotNull(message = "Время начала занятия не может быть пустым")
        @FutureOrPresent(message = "Время начала занятия должно быть в будущем или настоящем")
        LocalDateTime startTime,

        @Min(value = 1, message = "Продолжительность занятия должна быть не менее 1 минуты")
        @NotNull(message = "Продолжительность занятия не может быть пустой")
        Integer durationMinutes,

        @Min(value = 1, message = "Максимальное количество участников должно быть не менее 1")
        Integer maxParticipants,

        String location) {
}
