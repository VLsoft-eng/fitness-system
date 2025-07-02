package ru.hits.fitnesssystem.rest.controller;

import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.hits.fitnesssystem.core.service.EnrollmentStatisticsService;
import ru.hits.fitnesssystem.rest.model.EnrollmentStatisticsDto;

@RestController
@RequestMapping("/statistics/enrollments")
@RequiredArgsConstructor
public class EnrollmentStatisticsController {
    private final EnrollmentStatisticsService enrollmentStatisticsService;

    @GetMapping("/overview")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<EnrollmentStatisticsDto> getEnrollmentStatistics(
            @Parameter(description = "Предопределенный период (LAST_DAY, LAST_WEEK, LAST_MONTH, ALL_TIME). " +
                    "Либо укажите startDate и endDate.")
            @RequestParam(required = false) String period,
            @Parameter(description = "Начальная дата периода (YYYY-MM-DD), если period не указан")
            @RequestParam(required = false) String startDate,
            @Parameter(description = "Конечная дата периода (YYYY-MM-DD), если period не указан")
            @RequestParam(required = false) String endDate
    ) {
        EnrollmentStatisticsDto statistics = enrollmentStatisticsService.getEnrollmentStatistics(period, startDate, endDate);
        return ResponseEntity.ok(statistics);
    }
}
