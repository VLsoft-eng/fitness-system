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

    @PreAuthorize("hasRole('TRAINER')")
    @GetMapping("/trainer/my-enrollments")
    @ResponseStatus(HttpStatus.OK)
    public EnrollmentListDto getTrainerEnrollments() {
        return enrollmentService.getTrainerEnrollments();
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

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/is-exists-by-training-session-for-user/{trainingSessionId}")
    @ResponseStatus(HttpStatus.OK)
    public boolean isExistsEnrollmentByTrainingSessionId(@PathVariable Long trainingSessionId) {
        return enrollmentService.isTrainingSessionAssigned(trainingSessionId);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'TRAINER')")
    @PostMapping("/make-enrollment-for-session-for-user/{sessionId}/{userToAssignId}")
    @ResponseStatus(HttpStatus.OK)
    public void assignEnrollment(@PathVariable Long sessionId, @PathVariable Long userToAssignId) {
        enrollmentService.assignUserToSession(sessionId, userToAssignId);
    }


    @PostMapping("/approve-enrollment/{enrollmentId}")
    @PreAuthorize("isAuthenticated()")
    public void approveEnrollment(@PathVariable Long enrollmentId) {
        enrollmentService.approveEnrollment(enrollmentId);
    }
}
