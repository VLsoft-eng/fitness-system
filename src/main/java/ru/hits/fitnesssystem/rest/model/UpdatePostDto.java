package ru.hits.fitnesssystem.rest.model;

import jakarta.validation.constraints.Size;

public record UpdatePostDto(
        @Size(max = 255, message = "Заголовок не может быть длиннее 255 символов")
        String title,

        String description,

        String imageBase64
) {}