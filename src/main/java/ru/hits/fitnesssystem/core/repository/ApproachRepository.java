package ru.hits.fitnesssystem.core.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.hits.fitnesssystem.core.entity.Approach;

public interface ApproachRepository extends JpaRepository<Approach, Long> {
}
