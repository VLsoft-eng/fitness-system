package ru.hits.fitnesssystem.rest.model;

public record UserLoginDto(
        String username,
        String password
) {
}
