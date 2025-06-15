package ru.hits.fitnesssystem.rest.error;

import jakarta.validation.ConstraintViolation;
import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.context.request.WebRequest;
import ru.hits.fitnesssystem.rest.model.ApiErrorResponse;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class ErrorUtils {

    private static final String VALIDATION_ERROR_NAME = "Validation error";
    private static final String VALIDATION_ERROR_MESSAGE = "There were validation errors in the request";

    public static ApiErrorResponse createValidationErrorResponse(
            MethodArgumentNotValidException ex,
            WebRequest request
    ) {
        Map<String, String> errors = new HashMap<>();
        for (ObjectError error : ex.getBindingResult().getAllErrors()) {
            if (error instanceof FieldError fieldError) {
                String field = fieldError.getField();
                String errorMessage = fieldError.getDefaultMessage();
                errors.put(field, errorMessage != null ? errorMessage : "Error occurred");
            }
        }

        return createErrorResponse(
                HttpStatus.BAD_REQUEST,
                VALIDATION_ERROR_NAME,
                VALIDATION_ERROR_MESSAGE,
                errors,
                request
        );
    }

    public static ApiErrorResponse createConstraintViolationErrorResponse(
            Set<ConstraintViolation<?>> violations,
            WebRequest request
    ) {
        Map<String, String> errors = new HashMap<>();
        for (ConstraintViolation<?> violation : violations) {
            String fieldName = violation.getPropertyPath().toString();
            String errorMessage = violation.getMessage();
            errors.put(fieldName, errorMessage != null ? errorMessage : "Error occurred");
        }

        return createErrorResponse(
                HttpStatus.BAD_REQUEST,
                VALIDATION_ERROR_NAME,
                VALIDATION_ERROR_MESSAGE,
                errors,
                request
        );
    }

    public static ApiErrorResponse createSimpleErrorResponse(
            HttpStatus status,
            String error,
            String message,
            WebRequest request
    ) {
        return createErrorResponse(status, error, message, Map.of(), request);
    }

    private static ApiErrorResponse createErrorResponse(
            HttpStatus status,
            String error,
            String message,
            Map<String, String> details,
            WebRequest request
    ) {
        return new ApiErrorResponse(
                LocalDateTime.now(),
                status.value(),
                error,
                message,
                details,
                request.getDescription(false)
        );
    }
}

