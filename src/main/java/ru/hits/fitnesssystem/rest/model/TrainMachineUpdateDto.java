package ru.hits.fitnesssystem.rest.model;

import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public record TrainMachineUpdateDto(@Size(max = 100, message = "Название тренажера не может быть длиннее 100 символов")
                                    String name,

                                    String description,

                                    String base64Image,

                                    @Positive(message = "Количество должно быть положительным числом")
                                    Long count) {
}
