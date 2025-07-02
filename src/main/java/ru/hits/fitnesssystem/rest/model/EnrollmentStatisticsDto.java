package ru.hits.fitnesssystem.rest.model;

import lombok.Builder;

import java.util.Map;

@Builder
public record EnrollmentStatisticsDto(Long enrollmentsCountInPeriod,
                                      Map<String, Long> enrollmentsCountByStatus) {
}
