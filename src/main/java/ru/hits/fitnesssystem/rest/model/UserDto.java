package ru.hits.fitnesssystem.rest.model;

import ru.hits.fitnesssystem.core.enumeration.Gender;

import java.time.LocalDate;
import java.util.Optional;

public record UserDto(
        String username,
        String firstname,
        String lastname,
        Gender gender,
        Optional<LocalDate> birthday
) {
}
