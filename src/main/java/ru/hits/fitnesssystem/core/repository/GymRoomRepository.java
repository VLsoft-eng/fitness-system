package ru.hits.fitnesssystem.core.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.hits.fitnesssystem.core.entity.GymRoom;

import java.util.List;

@Repository
public interface GymRoomRepository extends JpaRepository<GymRoom, Long> {
    @Query("SELECT gr FROM GymRoom gr WHERE LOWER(gr.name) LIKE LOWER(CONCAT('%', :name, '%'))")
    List<GymRoom> findByNameContainingIgnoreCase(@Param("name") String name);
}
