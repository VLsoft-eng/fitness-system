package ru.hits.fitnesssystem.core.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.hits.fitnesssystem.core.entity.Enrollment;
import ru.hits.fitnesssystem.core.entity.TrainingSession;
import ru.hits.fitnesssystem.core.entity.User;
import ru.hits.fitnesssystem.core.enumeration.EnrollmentStatus;

import java.util.List;
import java.util.Optional;

@Repository
public interface EnrollmentRepository extends JpaRepository<Enrollment, Long> {

    List<Enrollment> findAllByUser(User user);

    Optional<Enrollment> findByUserAndTrainingSession(User user, TrainingSession trainingSession);

    List<Enrollment> findAllByTrainingSession(TrainingSession trainingSession);

    List<Enrollment> findAllByTrainingSessionAndStatusOrderByEnrollmentTimeAsc(TrainingSession trainingSession, EnrollmentStatus status);

    boolean existsByUserAndTrainingSessionAndStatusIn(User user, TrainingSession trainingSession, List<EnrollmentStatus> statuses);
}
