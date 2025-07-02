package ru.hits.fitnesssystem.core.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.hits.fitnesssystem.core.entity.GymRoom;
import ru.hits.fitnesssystem.core.entity.TrainMachine;
import ru.hits.fitnesssystem.core.exception.NotFoundException;
import ru.hits.fitnesssystem.core.repository.GymRoomRepository;
import ru.hits.fitnesssystem.core.repository.TrainMachineRepository;
import ru.hits.fitnesssystem.rest.model.TrainMachineCreateDto;
import ru.hits.fitnesssystem.rest.model.TrainMachineDto;
import ru.hits.fitnesssystem.rest.model.TrainMachineListDto;
import ru.hits.fitnesssystem.rest.model.TrainMachineUpdateDto;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TrainMachineService {
    private final TrainMachineRepository trainMachineRepository;
    private final GymRoomRepository gymRoomRepository;

    @Transactional
    public TrainMachineDto createTrainMachine(TrainMachineCreateDto createDto) {
        GymRoom gymRoom = gymRoomRepository.findById(createDto.gymRoomId())
                .orElseThrow(() -> new NotFoundException("Тренажерный зал с ID " + createDto.gymRoomId() + " не найден."));

        TrainMachine trainMachine = TrainMachine.builder()
                .name(createDto.name())
                .description(createDto.description())
                .base64Image(createDto.base64Image())
                .count(createDto.count())
                .gymRoom(gymRoom)
                .build();
        trainMachineRepository.save(trainMachine);
        return mapToDto(trainMachine);
    }

    @Transactional
    public TrainMachineDto updateTrainMachine(Long id, TrainMachineUpdateDto updateDto) {
        TrainMachine trainMachine = trainMachineRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Тренажер с ID " + id + " не найден."));

        if (updateDto.name() != null) {
            trainMachine.setName(updateDto.name());
        }
        if (updateDto.description() != null) {
            trainMachine.setDescription(updateDto.description());
        }
        if (updateDto.base64Image() != null) {
            trainMachine.setBase64Image(updateDto.base64Image());
        } else if (trainMachine.getBase64Image() != null) {
            trainMachine.setBase64Image(null);
        }
        if (updateDto.count() != null) {
            trainMachine.setCount(updateDto.count());
        }

        trainMachineRepository.save(trainMachine);
        return mapToDto(trainMachine);
    }

    @Transactional
    public void deleteTrainMachine(Long id) {
        if (!trainMachineRepository.existsById(id)) {
            throw new NotFoundException("Тренажер с ID " + id + " не найден.");
        }
        trainMachineRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public TrainMachineDto getTrainMachineById(Long id) {
        TrainMachine trainMachine = trainMachineRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Тренажер с ID " + id + " не найден."));
        return mapToDto(trainMachine);
    }

    @Transactional(readOnly = true)
    public TrainMachineListDto getAllTrainMachines(String name, Long gymRoomId) {
        List<TrainMachine> trainMachines;

        if (gymRoomId != null) {
            if (!gymRoomRepository.existsById(gymRoomId)) {
                throw new NotFoundException("Тренажерный зал с ID " + gymRoomId + " не найден.");
            }
            if (name != null && !name.trim().isEmpty()) {
                trainMachines = trainMachineRepository.findByGymRoomIdAndNameContainingIgnoreCase(gymRoomId, name.trim());
            } else {
                trainMachines = trainMachineRepository.findByGymRoomId(gymRoomId);
            }
        } else {
            if (name != null && !name.trim().isEmpty()) {
                trainMachines = trainMachineRepository.findByNameContainingIgnoreCase(name.trim());
            } else {
                trainMachines = trainMachineRepository.findAll();
            }
        }

        List<TrainMachineDto> trainMachineDtos = trainMachines.stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
        return new TrainMachineListDto(trainMachineDtos);
    }

    private TrainMachineDto mapToDto(TrainMachine trainMachine) {
        return TrainMachineDto.builder()
                .id(trainMachine.getId())
                .name(trainMachine.getName())
                .description(trainMachine.getDescription())
                .base64Image(trainMachine.getBase64Image())
                .count(trainMachine.getCount())
                .gymRoomId(trainMachine.getGymRoom() != null ? trainMachine.getGymRoom().getId() : null)
                .build();
    }
}
