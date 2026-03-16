package com.sentinel.api.controller;

import com.sentinel.api.dto.in.TaskRequestDTO;
import com.sentinel.api.dto.out.TaskResponseDTO;
import com.sentinel.api.entity.Machine;
import com.sentinel.api.entity.Task;
import com.sentinel.api.repository.MachineRepository;
import com.sentinel.api.repository.TaskRepository;
import com.sentinel.api.service.TaskProducerService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/tasks")
public class TaskController {

    private final TaskRepository taskRepository;
    private final MachineRepository machineRepository;
    private final TaskProducerService taskProducerService;

    public TaskController(TaskRepository taskRepository, MachineRepository machineRepository, TaskProducerService taskProducerService) {
        this.taskRepository = taskRepository;
        this.machineRepository = machineRepository;
        this.taskProducerService = taskProducerService;
    }

    // 1. Create task
    @PostMapping("/machine/{machineId}")
    public TaskResponseDTO createTask(@PathVariable Long machineId, @RequestBody TaskRequestDTO dtoIn) {
        Machine machine = machineRepository.findById(machineId)
                .orElseThrow(() -> new RuntimeException("Machine not found!"));

        Task newTask = new Task();
        newTask.setCommand(dtoIn.command());
        newTask.setStatus("PENDING"); // Always starts as pending
        newTask.setMachine(machine);

        Task taskSaved= taskRepository.save(newTask);

        return new TaskResponseDTO(taskSaved.getId(), taskSaved.getCommand(), taskSaved.getStatus(),
                taskSaved.getOutputLog(), taskSaved.getCreatedAt(), taskSaved.getMachine().getId());
    }

    // 2. Get pending Tasks
    @GetMapping("/machine/{machineId}/pending")
    public List<TaskResponseDTO> getPendingTasksForMachine(@PathVariable Long machineId) {
        List<Task> tasksInDB = taskRepository.findByMachineIdAndStatus(machineId, "PENDING");

        return tasksInDB.stream()
                .map(task -> new TaskResponseDTO(task.getId(), task.getCommand(), task.getStatus(),
                        task.getOutputLog(), task.getCreatedAt(), task.getMachine().getId()))
                .collect(Collectors.toList());
    }

    // 3. Update Status/Log
    @PutMapping("/{taskId}/status")
    public ResponseEntity<Void> updateTaskStatus(@PathVariable Long taskId, @RequestBody TaskRequestDTO dtoIn) {

        taskProducerService.sendTaskResultToQueue(taskId, dtoIn.status(), dtoIn.outputLog());

        // Returns a 202 ACCEPTED HTTP status, meaning: "I received it and will process it in the background"
        return ResponseEntity.accepted().build();
    }
}