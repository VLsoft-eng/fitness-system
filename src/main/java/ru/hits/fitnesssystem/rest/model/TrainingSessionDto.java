package ru.hits.fitnesssystem.rest.model;

import ru.hits.fitnesssystem.core.entity.TrainingSession;
import ru.hits.fitnesssystem.core.enumeration.TrainingSessionType;

import java.time.LocalDateTime;
import java.util.Optional;

public record TrainingSessionDto(
        Long id,
        String name,
        String description,
        TrainingSessionType type,
        UserDto trainer,
        LocalDateTime startTime,
        LocalDateTime endTime,
        Integer durationMinutes,
        Optional<Integer> maxParticipants,
        Integer currentParticipants,
        String location,
        boolean isFull,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
    public static TrainingSessionDto fromEntity(TrainingSession session) {
        UserDto trainerDto = (session.getTrainer() != null)
                ? new UserDto(
                session.getTrainer().getId(),
                session.getTrainer().getUsername(),
                session.getTrainer().getFirstName(),
                session.getTrainer().getLastName(),
                session.getTrainer().getGender(),
                session.getTrainer().getRole(),
                Optional.ofNullable(session.getTrainer().getBirthday()),
                session.getTrainer().getAvatarBase64())
                : null;

        boolean isFull = session.getMaxParticipants() != null &&
                session.getCurrentParticipants() >= session.getMaxParticipants();

        return new TrainingSessionDto(
                session.getId(),
                session.getName(),
                session.getDescription(),
                session.getType(),
                trainerDto,
                session.getStartTime(),
                session.getEndTime(),
                session.getDurationMinutes(),
                Optional.ofNullable(session.getMaxParticipants()),
                session.getCurrentParticipants(),
                session.getLocation(),
                isFull,
                session.getCreatedAt(),
                session.getUpdatedAt()
        );
    }
}
