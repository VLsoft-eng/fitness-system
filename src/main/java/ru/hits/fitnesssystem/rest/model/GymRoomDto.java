package ru.hits.fitnesssystem.rest.model;

import lombok.Builder;

import java.util.List;

@Builder
public record GymRoomDto(Long id,
                         String name,
                         String description,
                         Double longitude,
                         Double latitude,
                         Long capacity,
                         String base64Image,
                         List<TrainMachineDto> trainMachines ) {
}
