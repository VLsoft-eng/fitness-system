package ru.hits.fitnesssystem.core.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.hits.fitnesssystem.core.entity.TrainingSession;
import ru.hits.fitnesssystem.core.entity.User;
import ru.hits.fitnesssystem.core.enumeration.EnrollmentStatus;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface TrainingSessionRepository extends JpaRepository<TrainingSession, Long> {

    List<TrainingSession> findAllByTrainer(User trainer);

    Page<TrainingSession> findAllByStartTimeBetween(LocalDateTime start, LocalDateTime end, Pageable pageable);

    Page<TrainingSession> findAllByTrainerAndStartTimeBetween(User trainer, LocalDateTime start, LocalDateTime end, Pageable pageable);

    Page<TrainingSession> findAllByTrainer(User trainer, Pageable pageable);

    @Query("SELECT COUNT(ts) > 0 FROM TrainingSession ts WHERE ts.trainer = :trainer AND ts.id <> :sessionId AND " +
            "((ts.startTime < :endTime AND ts.endTime > :startTime))")
    boolean existsConflictingSessionForTrainer(@Param("trainer") User trainer,
                                               @Param("startTime") LocalDateTime startTime,
                                               @Param("endTime") LocalDateTime endTime,
                                               @Param("sessionId") Long sessionId);

    @Query("SELECT COUNT(e) > 0 FROM Enrollment e WHERE e.trainingSession.id = :sessionId AND e.status = :status")
    boolean existsByIdAndEnrollmentsStatus(@Param("sessionId") Long sessionId, @Param("status") EnrollmentStatus status);
}