package ru.hits.fitnesssystem.rest.model;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import ru.hits.fitnesssystem.core.enumeration.Gender;

public record UserRegistrationDto(
        @NotBlank(message = "Юзернейм не может быть пустым")
        String username,
        @NotBlank(message = "Имя не может быть пустым")
        String firstname,
        @NotBlank(message = "Фамилия не может быть пустым")
        String lastname,
        @NotBlank(message = "Пароль не может быть пустым")
        String password,
        @NotNull(message = "Гендер не может быть пустым")
        Gender gender
) {}
