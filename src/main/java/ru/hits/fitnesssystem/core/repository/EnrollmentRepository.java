package ru.hits.fitnesssystem.core.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.hits.fitnesssystem.core.entity.Enrollment;
import ru.hits.fitnesssystem.core.entity.TrainingSession;
import ru.hits.fitnesssystem.core.entity.User;
import ru.hits.fitnesssystem.core.enumeration.EnrollmentCallType;
import ru.hits.fitnesssystem.core.enumeration.EnrollmentStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface EnrollmentRepository extends JpaRepository<Enrollment, Long> {

    List<Enrollment> findAllByUser(User user);

    Optional<Enrollment> findByUserAndTrainingSession(User user, TrainingSession trainingSession);

    List<Enrollment> findAllByTrainingSession(TrainingSession trainingSession);

    List<Enrollment> findAllByTrainingSessionAndStatusOrderByEnrollmentTimeAsc(TrainingSession trainingSession, EnrollmentStatus status);

    boolean existsByUserAndTrainingSessionAndStatusIn(User user, TrainingSession trainingSession, List<EnrollmentStatus> statuses);

    boolean existsByUserIdAndTrainingSessionIdAndStatusNot(Long userId, Long trainingSessionId, EnrollmentStatus status);

    Long countByEnrollmentTimeBetween(LocalDateTime start, LocalDateTime end);

    @Query("SELECT e.status, COUNT(e) FROM Enrollment e GROUP BY e.status")
    List<Object[]> countEnrollmentsByStatus();

    @Query("SELECT e FROM Enrollment e WHERE e.trainingSession.trainer = :trainer AND e.enrollmentCallType = :callType")
    List<Enrollment> findAllByTrainerAndEnrollmentCallType(User trainer, EnrollmentCallType callType);

}
