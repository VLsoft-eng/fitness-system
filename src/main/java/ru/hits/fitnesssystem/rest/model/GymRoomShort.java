package ru.hits.fitnesssystem.rest.model;

public record GymRoomShort(Long id,
                           String name,
                           String description,
                           Double longitude,
                           Double latitude,
                           Long capacity) {
}
