package ru.hits.fitnesssystem.rest.model;

import java.time.LocalDateTime;
import java.util.Map;

public record ApiErrorResponse(
        LocalDateTime timestamp,
        int status,
        String error,
        String message,
        Map<String, String> errorDetails,
        String path
) {}
