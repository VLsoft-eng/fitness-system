package ru.hits.fitnesssystem.rest.controller;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.hits.fitnesssystem.core.service.GymStatisticsService;
import ru.hits.fitnesssystem.rest.model.GymRoomStatisticsDto;

@RestController
@RequestMapping("/statistics/gyms")
@RequiredArgsConstructor
public class GymStatisticsController {
    private final GymStatisticsService gymStatisticsService;

    @Operation(summary = "Получить статистику по залам и тренажерам")
    @GetMapping("/overview")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<GymRoomStatisticsDto> getGymRoomAndMachineStatistics() {
        GymRoomStatisticsDto statistics = gymStatisticsService.getGymRoomAndMachineStatistics();
        return ResponseEntity.ok(statistics);
    }
}
