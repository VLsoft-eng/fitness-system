package ru.hits.fitnesssystem.rest.model;

import lombok.Builder;

@Builder
public record TrainMachineDto(Long id,
                              String name,
                              String description,
                              String base64Image,
                              Long count,
                              Long gymRoomId ) {
}
