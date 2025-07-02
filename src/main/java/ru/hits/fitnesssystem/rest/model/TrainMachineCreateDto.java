package ru.hits.fitnesssystem.rest.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public record TrainMachineCreateDto(@NotBlank(message = "Название тренажера не может быть пустым")
                                    @Size(max = 100, message = "Название тренажера не может быть длиннее 100 символов")
                                    String name,

                                    String description,

                                    String base64Image,

                                    @NotNull(message = "Количество не может быть пустым")
                                    @Positive(message = "Количество должно быть положительным числом")
                                    Long count,

                                    @NotNull(message = "ID зала не может быть пустым")
                                    @Positive(message = "ID зала должен быть положительным числом")
                                    Long gymRoomId
){}
