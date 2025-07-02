package ru.hits.fitnesssystem.rest.model;

import lombok.Builder;

import java.util.Map;

@Builder
public record UserStatisticsDto(Long totalRegisteredUsers,
                                Long totalActiveUsers,
                                Map<String, Long> usersCountByRole) {
}
