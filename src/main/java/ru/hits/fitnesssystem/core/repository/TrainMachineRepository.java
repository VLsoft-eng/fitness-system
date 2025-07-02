package ru.hits.fitnesssystem.core.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.hits.fitnesssystem.core.entity.TrainMachine;

import java.util.List;

@Repository
public interface TrainMachineRepository extends JpaRepository<TrainMachine, Long> {
    List<TrainMachine> findByGymRoomId(Long gymRoomId);

    @Query("SELECT tm FROM TrainMachine tm WHERE tm.gymRoom.id = :gymRoomId AND LOWER(tm.name) LIKE LOWER(CONCAT('%', :name, '%'))")
    List<TrainMachine> findByGymRoomIdAndNameContainingIgnoreCase(@Param("gymRoomId") Long gymRoomId, @Param("name") String name);

    List<TrainMachine> findByNameContainingIgnoreCase(String name);

    @Query("SELECT SUM(tm.count) FROM TrainMachine tm")
    Long sumAllTrainMachineCounts();

    @Query("SELECT tm.gymRoom.name, SUM(tm.count) FROM TrainMachine tm GROUP BY tm.gymRoom.name")
    List<Object[]> sumTrainMachineCountsByGymRoom();
}
