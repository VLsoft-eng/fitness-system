package ru.hits.fitnesssystem.rest.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreatePostDto(
        @NotBlank(message = "Заголовок не может быть пустым")
        @Size(max = 255, message = "Заголовок не может быть длиннее 255 символов")
        String title,

        @NotBlank(message = "Описание не может быть пустым")
        String description,

        String imageBase64
) {}