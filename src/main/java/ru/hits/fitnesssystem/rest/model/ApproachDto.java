package ru.hits.fitnesssystem.rest.model;

import java.math.BigDecimal;

public record ApproachDto(
        Long id,
        BigDecimal approachesCount,
        BigDecimal repetitionPerApproachCount
) {
}
