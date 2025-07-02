package ru.hits.fitnesssystem.rest.model;

import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public record GymRoomUpdateDto(@Size(max = 100, message = "Имя зала не может быть длиннее 100 символов")
                                String name,

                               String description,

                               Double longitude,

                               Double latitude,

                               @Positive(message = "Вместимость должна быть положительным числом")
                                Long capacity,

                               String base64Image) {
}
