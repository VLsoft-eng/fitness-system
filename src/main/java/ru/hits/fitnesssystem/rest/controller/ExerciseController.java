package ru.hits.fitnesssystem.rest.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.hits.fitnesssystem.core.service.ExerciseService;
import ru.hits.fitnesssystem.rest.model.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/exercises")
public class ExerciseController {

    private final ExerciseService exerciseService;

    @PostMapping
    public ResponseEntity<ExerciseDto> createExercise(@RequestBody @Valid ExerciseCreateDto exerciseCreateDto) {
        ExerciseDto created = exerciseService.createExercise(exerciseCreateDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @GetMapping
    public ResponseEntity<ExerciseListDto> getExercisesForCurrentTrainer() {
        ExerciseListDto list = exerciseService.getExercisesForCurrentTrainer();
        return ResponseEntity.ok(list);
    }

    @PostMapping("/approaches")
    public ResponseEntity<ApproachDto> createApproach(@RequestBody @Valid ApproachCreateDto approachCreateDto) {
        ApproachDto created = exerciseService.createApproach(approachCreateDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @GetMapping("/approaches")
    public ResponseEntity<ApproachesListDto> getApproachesForCurrentTrainer() {
        ApproachesListDto list = exerciseService.getApproachesForCurrentTrainer();
        return ResponseEntity.ok(list);
    }

    @PostMapping("/full")
    public ResponseEntity<FullExerciseDto> createFullExercise(@RequestBody @Valid FullExerciseCreateDto fullExerciseCreateDto) {
        FullExerciseDto created = exerciseService.createFullExercise(fullExerciseCreateDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @GetMapping("/full/{id}")
    public ResponseEntity<FullExerciseDto> getFullExerciseById(@PathVariable Long id) {
        FullExerciseDto dto = exerciseService.getFullExerciseById(id);
        return ResponseEntity.ok(dto);
    }
}
