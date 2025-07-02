package ru.hits.fitnesssystem.core.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.hits.fitnesssystem.core.enumeration.EnrollmentStatus;
import ru.hits.fitnesssystem.core.repository.EnrollmentRepository;
import ru.hits.fitnesssystem.rest.model.EnrollmentStatisticsDto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EnrollmentStatisticsService {
    private final EnrollmentRepository enrollmentRepository;

    @Transactional(readOnly = true)
    public EnrollmentStatisticsDto getEnrollmentStatistics(String period, String startDateStr, String endDateStr) {
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
                case "ALL_TIME":
                    start = LocalDateTime.MIN;
                    end = LocalDateTime.MAX;
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

        Long enrollmentsCountInPeriod = enrollmentRepository.countByEnrollmentTimeBetween(start, end);

        Map<String, Long> enrollmentsCountByStatus = new HashMap<>();
        List<Object[]> enrollmentsStatusResults = enrollmentRepository.countEnrollmentsByStatus();
        if (enrollmentsStatusResults != null) {
            enrollmentsCountByStatus = enrollmentsStatusResults.stream()
                    .collect(Collectors.toMap(
                            arr -> ((EnrollmentStatus) arr[0]).name(),
                            arr -> (Long) arr[1]
                    ));
        }

        return EnrollmentStatisticsDto.builder()
                .enrollmentsCountInPeriod(enrollmentsCountInPeriod)
                .enrollmentsCountByStatus(enrollmentsCountByStatus)
                .build();
    }
}
