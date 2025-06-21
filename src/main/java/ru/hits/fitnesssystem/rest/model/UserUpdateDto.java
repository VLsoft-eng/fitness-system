package ru.hits.fitnesssystem.rest.model;

import ru.hits.fitnesssystem.core.enumeration.Gender;

import java.time.LocalDate;

public record UserUpdateDto(
        String firstname,
        String lastname,
        Gender gender,
        LocalDate birthday,
        String avatarBase64
) {}