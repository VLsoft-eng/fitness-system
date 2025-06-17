package ru.hits.fitnesssystem.rest.model;

import ru.hits.fitnesssystem.core.enumeration.UserRole;

public record ChangeUserRoleDto(
        Long userId,
        UserRole newRole
) {
}
