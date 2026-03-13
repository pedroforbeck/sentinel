package com.sentinel.api.controller;


import com.sentinel.api.dto.in.TaskRequestDTO;
import com.sentinel.api.dto.out.TaskResponseDTO;
import com.sentinel.api.model.Machine;
import com.sentinel.api.model.Task;
import com.sentinel.api.repository.MachineRepository;
import com.sentinel.api.repository.TaskRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/tasks")
public class TaskController {

    private final TaskRepository taskRepository;
    private final MachineRepository machineRepository;

    public TaskController(TaskRepository taskRepository, MachineRepository machineRepository) {
        this.taskRepository = taskRepository;
        this.machineRepository = machineRepository;
    }

    // 1. Create task
    @PostMapping("/machine/{machineId}")
    public TaskResponseDTO createTask(@PathVariable Long machineId, @RequestBody TaskRequestDTO dtoIn) {
        Machine machine = machineRepository.findById(machineId)
                .orElseThrow(() -> new RuntimeException("Machine not found!"));

        Task newTask = new Task();
        newTask.setCommand(dtoIn.command());
        newTask.setStatus(dtoIn.status());
        newTask.setMachine(machine);

        Task taskSaved= taskRepository.save(newTask);

        return new TaskResponseDTO(taskSaved.getId(), taskSaved.getCommand(), taskSaved.getStatus(),
                taskSaved.getOutputLog(), taskSaved.getCreatedAt(), taskSaved.getMachine().getId());
    }

    // 2. Get pending Tasks
    @GetMapping("/machine/{machineId}/pending")
    public List<TaskResponseDTO> getPendingTasksForMachine(@PathVariable Long machineId) {
        List<Task> tasksInDB = taskRepository.findByMachineIdAndStatus(machineId, "PENDING");

        return tasksInDB .stream()
                .map(task -> new TaskResponseDTO(task.getId(), task.getCommand(), task.getStatus(),
                        task.getOutputLog(), task.getCreatedAt(), task.getMachine().getId()))
                .collect(Collectors.toList());
    }

    // 3. Update Status/Log
    @PutMapping("/{taskId}/status")
    public TaskResponseDTO updateTaskStatus(@PathVariable Long taskId, @RequestBody TaskRequestDTO dtoIn) {
        return taskRepository.findById(taskId)
                .map(existingTask -> {
                    existingTask.setStatus(dtoIn.status());
                    existingTask.setOutputLog(dtoIn.outputLog());

                    Task updatedTask = taskRepository.save(existingTask);

                    return new TaskResponseDTO(updatedTask.getId(), updatedTask.getCommand(),
                            updatedTask.getStatus(), updatedTask.getOutputLog(),
                            updatedTask.getCreatedAt(), updatedTask.getMachine().getId());
                })
                .orElseThrow(() -> new RuntimeException("Task not found!"));
    }
}