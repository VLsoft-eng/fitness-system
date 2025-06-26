package ru.hits.fitnesssystem.rest.model;

import java.util.List;

public record FullExercisesListDto(
        List<FullExerciseDto> fullExercises
) {}