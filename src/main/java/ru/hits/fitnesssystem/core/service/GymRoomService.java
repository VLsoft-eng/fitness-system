package ru.hits.fitnesssystem.core.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.hits.fitnesssystem.core.entity.GymRoom;
import ru.hits.fitnesssystem.core.entity.TrainMachine;
import ru.hits.fitnesssystem.core.exception.NotFoundException;
import ru.hits.fitnesssystem.core.repository.GymRoomRepository;
import ru.hits.fitnesssystem.rest.model.*;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GymRoomService {
    private final GymRoomRepository gymRoomRepository;

    @Transactional
    public GymRoomDto createGymRoom(GymRoomCreateDto createDto) {
        GymRoom gymRoom = GymRoom.builder()
                .name(createDto.name())
                .description(createDto.description())
                .longitude(createDto.longitude())
                .latitude(createDto.latitude())
                .capacity(createDto.capacity())
                .base64Image(createDto.base64Image())
                .build();
        gymRoomRepository.save(gymRoom);
        return mapToDto(gymRoom);
    }

    @Transactional
    public GymRoomDto updateGymRoom(Long id, GymRoomUpdateDto updateDto) {
        GymRoom gymRoom = gymRoomRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Тренажерный зал с ID " + id + " не найден."));

        if (updateDto.name() != null) {
            gymRoom.setName(updateDto.name());
        }
        if (updateDto.description() != null) {
            gymRoom.setDescription(updateDto.description());
        }
        if (updateDto.longitude() != null) {
            gymRoom.setLongitude(updateDto.longitude());
        }
        if (updateDto.latitude() != null) {
            gymRoom.setLatitude(updateDto.latitude());
        }
        if (updateDto.capacity() != null) {
            gymRoom.setCapacity(updateDto.capacity());
        }
        if (updateDto.base64Image() != null) {
            gymRoom.setBase64Image(updateDto.base64Image());
        } else if (gymRoom.getBase64Image() != null) {
            gymRoom.setBase64Image(null);
        }

        gymRoomRepository.save(gymRoom);
        return mapToDto(gymRoom);
    }

    @Transactional
    public void deleteGymRoom(Long id) {
        if (!gymRoomRepository.existsById(id)) {
            throw new NotFoundException("Тренажерный зал с ID " + id + " не найден.");
        }
        gymRoomRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public GymRoomDto getGymRoomById(Long id) {
        GymRoom gymRoom = gymRoomRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Тренажерный зал с ID " + id + " не найден."));
        return mapToDto(gymRoom);
    }

    @Transactional(readOnly = true)
    public GymRoomListDto getAllGymRooms(String name) {
        List<GymRoom> gymRooms;
        if (name != null && !name.trim().isEmpty()) {
            gymRooms = gymRoomRepository.findByNameContainingIgnoreCase(name.trim());
        } else {
            gymRooms = gymRoomRepository.findAll();
        }
        List<GymRoomDto> gymRoomDtos = gymRooms.stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
        return new GymRoomListDto(gymRoomDtos);
    }

    private GymRoomDto mapToDto(GymRoom gymRoom) {
        List<TrainMachineDto> trainMachineDtos = gymRoom.getTrainMachines().stream()
                .map(this::mapTrainMachineToDto)
                .collect(Collectors.toList());

        return GymRoomDto.builder()
                .id(gymRoom.getId())
                .name(gymRoom.getName())
                .description(gymRoom.getDescription())
                .longitude(gymRoom.getLongitude())
                .latitude(gymRoom.getLatitude())
                .capacity(gymRoom.getCapacity())
                .base64Image(gymRoom.getBase64Image())
                .trainMachines(trainMachineDtos)
                .build();
    }

    private TrainMachineDto mapTrainMachineToDto(TrainMachine trainMachine) {
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
