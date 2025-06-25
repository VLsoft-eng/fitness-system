package ru.hits.fitnesssystem.core.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.hits.fitnesssystem.core.entity.Approach;

import java.util.List;

public interface ApproachRepository extends JpaRepository<Approach, Long> {
    List<Approach> findAllByTrainerId(Long trainerId);
}
