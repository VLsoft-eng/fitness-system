package ru.hits.fitnesssystem.rest.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.hits.fitnesssystem.core.service.TrainingSessionStatisticsService;
import ru.hits.fitnesssystem.rest.model.TrainingSessionStatisticsDto;

@RestController
@RequestMapping("/statistics/trainings")
@RequiredArgsConstructor
public class TrainingSessionStatisticsController {
    private final TrainingSessionStatisticsService trainingSessionStatisticsService;

    @GetMapping("/overview")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<TrainingSessionStatisticsDto> getTrainingSessionStatistics() {
        TrainingSessionStatisticsDto statistics = trainingSessionStatisticsService.getTrainingSessionStatistics();
        return ResponseEntity.ok(statistics);
    }
}
