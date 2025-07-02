package ru.hits.fitnesssystem.rest.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.hits.fitnesssystem.core.service.TrainMachineService;
import ru.hits.fitnesssystem.rest.model.TrainMachineCreateDto;
import ru.hits.fitnesssystem.rest.model.TrainMachineDto;
import ru.hits.fitnesssystem.rest.model.TrainMachineListDto;
import ru.hits.fitnesssystem.rest.model.TrainMachineUpdateDto;

@RestController
@RequestMapping("/train-machines")
@RequiredArgsConstructor
public class TrainMachineController {
    private final TrainMachineService trainMachineService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<TrainMachineDto> createTrainMachine(@RequestBody @Validated TrainMachineCreateDto createDto) {
        TrainMachineDto createdTrainMachine = trainMachineService.createTrainMachine(createDto);
        return ResponseEntity.status(HttpStatus.OK).body(createdTrainMachine);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<TrainMachineDto> updateTrainMachine(
            @PathVariable Long id,
            @RequestBody @Validated TrainMachineUpdateDto updateDto) {
        TrainMachineDto updatedTrainMachine = trainMachineService.updateTrainMachine(id, updateDto);
        return ResponseEntity.ok(updatedTrainMachine);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteTrainMachine(@PathVariable Long id) {
        trainMachineService.deleteTrainMachine(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<TrainMachineDto> getTrainMachineById(@PathVariable Long id) {
        TrainMachineDto trainMachine = trainMachineService.getTrainMachineById(id);
        return ResponseEntity.ok(trainMachine);
    }

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<TrainMachineListDto> getAllTrainMachines(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) Long gymRoomId) {
        TrainMachineListDto trainMachines = trainMachineService.getAllTrainMachines(name, gymRoomId);
        return ResponseEntity.ok(trainMachines);
    }
}
