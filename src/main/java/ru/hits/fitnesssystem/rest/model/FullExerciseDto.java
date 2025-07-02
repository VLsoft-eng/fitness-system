package ru.hits.fitnesssystem.rest.model;

public record FullExerciseDto(
        Long id,
        ExerciseDto exerciseDto,
        ApproachDto approachDto,
        TrainMachineDto trainMachineDto
) {
}
