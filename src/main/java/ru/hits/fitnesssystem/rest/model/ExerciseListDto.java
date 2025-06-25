package ru.hits.fitnesssystem.rest.model;

import java.util.List;

public record ExerciseListDto(
        List<ExerciseDto> exercises
){ }