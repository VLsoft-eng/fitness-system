package ru.hits.fitnesssystem.core.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.hits.fitnesssystem.core.enumeration.UserRole;
import ru.hits.fitnesssystem.core.repository.UserRepository;
import ru.hits.fitnesssystem.rest.model.NewRegistrationsPeriodDto;
import ru.hits.fitnesssystem.rest.model.UserStatisticsDto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserStatisticsService {
    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public UserStatisticsDto getUserOverviewStatistics() {
        Long totalUsers = userRepository.count();
        Long totalActiveUsers = userRepository.countActiveUsers();

        Map<String, Long> usersCountByRole = userRepository.countUsersByRole().stream()
                .collect(Collectors.toMap(
                        arr -> ((UserRole) arr[0]).name(),
                        arr -> (Long) arr[1]
                ));

        return UserStatisticsDto.builder()
                .totalRegisteredUsers(totalUsers)
                .totalActiveUsers(totalActiveUsers)
                .usersCountByRole(usersCountByRole)
                .build();
    }

    @Transactional(readOnly = true)
    public NewRegistrationsPeriodDto getNewRegistrations(String period, String startDateStr, String endDateStr) {
        LocalDateTime start;
        LocalDateTime end = LocalDateTime.now();

        if (period != null) {
            switch (period.toUpperCase()) {
                case "LAST_DAY":
                    start = LocalDate.now().atStartOfDay();
                    break;
                case "LAST_WEEK":
                    start = LocalDate.now().with(java.time.DayOfWeek.MONDAY).atStartOfDay();
                    break;
                case "LAST_MONTH":
                    start = LocalDate.now().withDayOfMonth(1).atStartOfDay();
                    break;
                default:
                    throw new IllegalArgumentException("Неизвестный период: " + period);
            }
        } else if (startDateStr != null && endDateStr != null) {
            try {
                start = LocalDate.parse(startDateStr).atStartOfDay();
                end = LocalDate.parse(endDateStr).atTime(23, 59, 59, 999999999);
            } catch (DateTimeParseException e) {
                throw new IllegalArgumentException("Неверный формат даты. Используйте YYYY-MM-DD.");
            }
        } else {
            start = LocalDate.now().withDayOfMonth(1).atStartOfDay();
        }

        Long count = userRepository.countByCreatedAtBetween(start, end);
        String actualPeriod = period != null ? period : (startDateStr + " to " + endDateStr);

        return new NewRegistrationsPeriodDto(actualPeriod, count);
    }
}
