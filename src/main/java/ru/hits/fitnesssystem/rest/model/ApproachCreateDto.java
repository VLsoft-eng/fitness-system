package ru.hits.fitnesssystem.rest.model;

import java.math.BigDecimal;

public record ApproachCreateDto(
        BigDecimal approachesCount,
        BigDecimal repetitionPerApproachCount
) {
}
