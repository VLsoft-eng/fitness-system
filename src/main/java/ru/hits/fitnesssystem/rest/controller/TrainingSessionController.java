package ru.hits.fitnesssystem.rest.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import ru.hits.fitnesssystem.core.service.TrainingSessionService;
import ru.hits.fitnesssystem.rest.model.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/training-sessions")
@RequiredArgsConstructor
public class TrainingSessionController {

    private final TrainingSessionService trainingSessionService;

    @PreAuthorize("hasAnyRole('ADMIN', 'TRAINER')")
    @PostMapping
    @ResponseStatus(HttpStatus.OK)
    public TrainingSessionDto createTrainingSession(@RequestBody @Valid CreateTrainingSessionDto dto) {
        return trainingSessionService.createTrainingSession(dto);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'TRAINER')")
    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public TrainingSessionDto updateTrainingSession(@PathVariable Long id, @RequestBody @Valid UpdateTrainingSessionDto dto) {
        return trainingSessionService.updateTrainingSession(id, dto);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'TRAINER')")
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteTrainingSession(@PathVariable Long id) {
        trainingSessionService.deleteTrainingSession(id);
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public TrainingSessionDto getTrainingSession(@PathVariable Long id) {
        return trainingSessionService.getTrainingSession(id);
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public TrainingSessionListDto getAllTrainingSessions(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "startTime,asc") String[] sort,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate,
            @RequestParam(required = false) Long trainerId
    ) {
        Sort.Direction direction = Sort.Direction.fromString(sort[1]);
        Sort sortObj = Sort.by(direction, sort[0]);
        Pageable pageable = PageRequest.of(page, size, sortObj);

        return trainingSessionService.getAllTrainingSessions(pageable, startDate, endDate, trainerId);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'TRAINER')")
    @PostMapping("/{id}/attach-full-exercise")
    public void attachFullExercise(@PathVariable Long id, @RequestBody @Valid AttachFullExerciseDto attachFullExerciseDto) {
        trainingSessionService.attachFullExercise(attachFullExerciseDto, id);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'TRAINER')")
    @PostMapping("/{id}/detach-full-exercise")
    public void disattachFullExercise(@PathVariable Long id, @RequestBody @Valid DetachFullExerciseDto detachFullExerciseDto) {
        trainingSessionService.detachFullExercise(id, detachFullExerciseDto.fullExerciseId());
    }
}