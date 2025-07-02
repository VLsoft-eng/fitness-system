package ru.hits.fitnesssystem.core.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.hits.fitnesssystem.core.repository.TrainMachineRepository;
import ru.hits.fitnesssystem.core.repository.TrainingSessionRepository;
import ru.hits.fitnesssystem.rest.model.GymRoomStatisticsDto;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GymStatisticsService {
    private final TrainingSessionRepository trainingSessionRepository;
    private final TrainMachineRepository trainMachineRepository;

    @Transactional(readOnly = true)
    public GymRoomStatisticsDto getGymRoomAndMachineStatistics() {
        Map<String, Long> popularGymRoomsByTrainingCount = new HashMap<>();
        List<Object[]> popularGymRoomsResults = trainingSessionRepository.countTrainingSessionsByGymRoom();
        if (popularGymRoomsResults != null) {
            popularGymRoomsByTrainingCount = popularGymRoomsResults.stream()
                    .collect(Collectors.toMap(
                            arr -> (String) arr[0],
                            arr -> (Long) arr[1]
                    ));
        }

        Map<String, Long> totalMachinesByGymRoom = new HashMap<>();
        List<Object[]> machinesByGymRoomResults = trainMachineRepository.sumTrainMachineCountsByGymRoom();
        if (machinesByGymRoomResults != null) {
            totalMachinesByGymRoom = machinesByGymRoomResults.stream()
                    .collect(Collectors.toMap(
                            arr -> (String) arr[0],
                            arr -> (Long) arr[1]
                    ));
        }

        Long totalMachinesOverall = trainMachineRepository.sumAllTrainMachineCounts();
        if (totalMachinesOverall == null) {
            totalMachinesOverall = 0L;
        }

        return GymRoomStatisticsDto.builder()
                .popularGymRoomsByTrainingCount(popularGymRoomsByTrainingCount)
                .totalMachinesByGymRoom(totalMachinesByGymRoom)
                .totalMachinesOverall(totalMachinesOverall)
                .build();
    }
}
