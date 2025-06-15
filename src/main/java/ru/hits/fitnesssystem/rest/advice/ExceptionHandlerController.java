package ru.hits.fitnesssystem.rest.advice;

import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import ru.hits.fitnesssystem.core.exception.BadRequestException;
import ru.hits.fitnesssystem.core.exception.NotFoundException;
import ru.hits.fitnesssystem.rest.error.ErrorUtils;
import ru.hits.fitnesssystem.rest.model.ApiErrorResponse;
import jakarta.validation.ConstraintViolationException;

@RestControllerAdvice
@ParameterObject
public class ExceptionHandlerController {

    private static final String NOT_FOUND_NAME = "Not found";
    private static final String BAD_REQUEST_ERROR_MESSAGE = "Bad Request";

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiErrorResponse> handleValidationException(
            MethodArgumentNotValidException ex, WebRequest request) {
        return ResponseEntity
                .badRequest()
                .body(ErrorUtils.createValidationErrorResponse(ex, request));
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ApiErrorResponse> handleConstraintViolationException(
            ConstraintViolationException ex, WebRequest request) {
        return ResponseEntity
                .badRequest()
                .body(ErrorUtils.createConstraintViolationErrorResponse(ex.getConstraintViolations(), request));
    }

    @ExceptionHandler(NotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ResponseEntity<ApiErrorResponse> handleNotFoundException(
            NotFoundException e, WebRequest request) {
        return createErrorResponse(HttpStatus.NOT_FOUND, NOT_FOUND_NAME, e.getMessage(), request);
    }

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ApiErrorResponse> handleBadRequestException(
            BadRequestException e, WebRequest request) {
        return createErrorResponse(HttpStatus.BAD_REQUEST, BAD_REQUEST_ERROR_MESSAGE, e.getMessage(), request);
    }

    private ResponseEntity<ApiErrorResponse> createErrorResponse(
            HttpStatus status, String error, String message, WebRequest request) {
        ApiErrorResponse response = ErrorUtils.createSimpleErrorResponse(
                status, error, message, request
        );
        return ResponseEntity.status(status).body(response);
    }
}
