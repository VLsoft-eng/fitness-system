package ru.hits.fitnesssystem.rest.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public record GymRoomCreateDto(@NotBlank(message = "Имя зала не может быть пустым")
                                @Size(max = 100, message = "Имя зала не может быть длиннее 100 символов")
                                String name,

                               String description,

                               @NotNull(message = "Долгота не может быть пустой")
                               Double longitude,

                               @NotNull(message = "Широта не может быть пустой")
                               Double latitude,

                               @NotNull(message = "Вместимость не может быть пустой")
                               @Positive(message = "Вместимость должна быть положительным числом")
                               Long capacity,

                               String base64Image) {
}
