package ru.hits.fitnesssystem.rest.model;

import lombok.Builder;

import java.util.Map;

@Builder
public record GymRoomStatisticsDto(Map<String, Long> popularGymRoomsByTrainingCount,
                                   Map<String, Long> totalMachinesByGymRoom,
                                   Long totalMachinesOverall) {
}
