package ru.hits.fitnesssystem.rest.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import ru.hits.fitnesssystem.core.service.EnrollmentService;
import ru.hits.fitnesssystem.rest.model.EnrollmentDto;
import ru.hits.fitnesssystem.rest.model.EnrollmentListDto;

@RestController
@RequestMapping("/enrollments")
@RequiredArgsConstructor
public class EnrollmentController {

    private final EnrollmentService enrollmentService;

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/{sessionId}")
    @ResponseStatus(HttpStatus.OK)
    public EnrollmentDto enrollUserToSession(@PathVariable Long sessionId) {
        return enrollmentService.enrollUserToSession(sessionId);
    }

    @PreAuthorize("isAuthenticated()")
    @DeleteMapping("/{enrollmentId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void cancelEnrollment(@PathVariable Long enrollmentId) {
        enrollmentService.cancelEnrollment(enrollmentId);
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/my")
    @ResponseStatus(HttpStatus.OK)
    public EnrollmentListDto getMyEnrollments() {
        return enrollmentService.getMyEnrollments();
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'TRAINER')")
    @GetMapping("/session/{sessionId}")
    @ResponseStatus(HttpStatus.OK)
    public EnrollmentListDto getSessionEnrollments(@PathVariable Long sessionId) {
        return enrollmentService.getSessionEnrollments(sessionId);
    }
}
