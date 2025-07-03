package ru.hits.fitnesssystem.rest.model;

import ru.hits.fitnesssystem.core.entity.Exercise;
import ru.hits.fitnesssystem.core.entity.TrainMachine;
import ru.hits.fitnesssystem.core.entity.TrainingSession;
import ru.hits.fitnesssystem.core.enumeration.TrainingSessionType;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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
        LocalDateTime updatedAt,
        List<FullExerciseDto> fullExercises
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

        var toExerciseDto = new java.util.function.Function<Exercise, ExerciseDto>() {
            @Override
            public ExerciseDto apply(Exercise exercise) {
                return new ExerciseDto(
                        exercise.getId(),
                        exercise.getTitle(),
                        exercise.getDescription()
                );
            }
        };


        List<FullExerciseDto> fullExerciseDtos = session.getFullExercises().stream()
                .map(fullExercise -> {
                    TrainMachineDto fullExerciseTrainingMachineDto = null;
                    TrainMachine trainingMachineEntity = fullExercise.getTrainingMachine();
                    if (trainingMachineEntity != null) {
                        fullExerciseTrainingMachineDto = new TrainMachineDto(
                                trainingMachineEntity.getId(),
                                trainingMachineEntity.getName(),
                                trainingMachineEntity.getDescription(),
                                trainingMachineEntity.getBase64Image(),
                                trainingMachineEntity.getCount(),
                                trainingMachineEntity.getGymRoom() != null ? trainingMachineEntity.getGymRoom().getId() : null
                        );
                    }

                    return new FullExerciseDto(
                            fullExercise.getId(),
                            toExerciseDto.apply(fullExercise.getExercise()),
                            new ApproachDto(
                                    fullExercise.getApproach().getId(),
                                    fullExercise.getApproach().getApproachesCount(),
                                    fullExercise.getApproach().getRepetitionPerApproachCount()
                            ),
                            fullExerciseTrainingMachineDto
                    );
                })
                .collect(Collectors.toList());

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
                session.getUpdatedAt(),
                fullExerciseDtos
        );
    }
}