package ru.hits.fitnesssystem.rest.model;

import ru.hits.fitnesssystem.core.enumeration.Gender;
import ru.hits.fitnesssystem.core.enumeration.UserRole;

import java.time.LocalDate;
import java.util.Optional;

public record UserDto(
        Long id,
        String username,
        String firstname,
        String lastname,
        Gender gender,
        UserRole userRole,
        Optional<LocalDate> birthday
) {
}
