package ru.hits.fitnesssystem.rest.model;

import lombok.Builder;

@Builder
public record NewRegistrationsPeriodDto(String period,
                                        Long count) {
}
