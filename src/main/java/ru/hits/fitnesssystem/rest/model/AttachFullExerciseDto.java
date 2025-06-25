package ru.hits.fitnesssystem.rest.model;

import java.util.List;

public record AttachFullExerciseDto(
        List<Long> exerciseIds
) {
}
