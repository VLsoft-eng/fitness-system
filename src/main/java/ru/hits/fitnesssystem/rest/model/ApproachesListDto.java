package ru.hits.fitnesssystem.rest.model;

import java.util.List;

public record ApproachesListDto(
        List<ApproachDto> approachDtos
) { }