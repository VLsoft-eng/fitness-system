package ru.hits.fitnesssystem.rest.controller;

import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.hits.fitnesssystem.core.service.UserStatisticsService;
import ru.hits.fitnesssystem.rest.model.NewRegistrationsPeriodDto;
import ru.hits.fitnesssystem.rest.model.UserStatisticsDto;

@RestController
@RequestMapping("/statistics/users")
@RequiredArgsConstructor
public class UserStatisticsController {
    private final UserStatisticsService userStatisticsService;

    @GetMapping("/overview")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserStatisticsDto> getUserOverviewStatistics() {
        UserStatisticsDto statistics = userStatisticsService.getUserOverviewStatistics();
        return ResponseEntity.ok(statistics);
    }

    @GetMapping("/new-registrations")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<NewRegistrationsPeriodDto> getNewRegistrations(
            @Parameter(description = "Предопределенный период (LAST_DAY, LAST_WEEK, LAST_MONTH). " +
                    "Либо укажите startDate и endDate.")
            @RequestParam(required = false) String period,
            @Parameter(description = "Начальная дата периода (YYYY-MM-DD), если period не указан")
            @RequestParam(required = false) String startDate,
            @Parameter(description = "Конечная дата периода (YYYY-MM-DD), если period не указан")
            @RequestParam(required = false) String endDate
    ) {
        NewRegistrationsPeriodDto statistics = userStatisticsService.getNewRegistrations(period, startDate, endDate);
        return ResponseEntity.ok(statistics);
    }
}
