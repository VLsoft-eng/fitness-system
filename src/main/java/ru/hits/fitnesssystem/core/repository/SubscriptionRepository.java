package ru.hits.fitnesssystem.core.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.hits.fitnesssystem.core.entity.Subscription;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface SubscriptionRepository extends JpaRepository<Subscription, Long> {
    Long countByIsActive(Boolean isActive);

    Long countByEndDateBetween(LocalDate start, LocalDate end);

    @Query("SELECT FUNCTION('TO_CHAR', s.startDate, 'YYYY-MM'), COUNT(s) FROM Subscription s GROUP BY FUNCTION('TO_CHAR', s.startDate, 'YYYY-MM') ORDER BY FUNCTION('TO_CHAR', s.startDate, 'YYYY-MM')")
    List<Object[]> countSubscriptionsByStartDateMonth();

    @Query("SELECT FUNCTION('TO_CHAR', s.endDate, 'YYYY-MM'), COUNT(s) FROM Subscription s GROUP BY FUNCTION('TO_CHAR', s.endDate, 'YYYY-MM') ORDER BY FUNCTION('TO_CHAR', s.endDate, 'YYYY-MM')")
    List<Object[]> countSubscriptionsByEndDateMonth();
}
