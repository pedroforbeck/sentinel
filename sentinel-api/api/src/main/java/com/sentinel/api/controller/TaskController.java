package com.sentinel.api.controller;

import com.sentinel.api.model.Machine;
import com.sentinel.api.model.Task;
import com.sentinel.api.repository.MachineRepository;
import com.sentinel.api.repository.TaskRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tasks")
public class TaskController {

    private final TaskRepository taskRepository;
    private final MachineRepository machineRepository;


    public TaskController(TaskRepository taskRepository, MachineRepository machineRepository) {
        this.taskRepository = taskRepository;
        this.machineRepository = machineRepository;
    }


    @PostMapping("/machine/{machineId}")
    public Task createTask(@PathVariable("machineId") Long machineId, @RequestBody Task task) {
        Machine machine = machineRepository.findById(machineId)
                .orElseThrow(() -> new RuntimeException("Machine not found!"));
        task.setMachine(machine);

        return taskRepository.save(task);

    }

    @GetMapping
    public List<Task> getAllTasks() {
        return taskRepository.findAll();
    }

    @GetMapping("/machine/{machineId}/pending")
    public List<Task> getPendingTasksForMachine(@PathVariable Long machineId) {
        return taskRepository.findByMachineIdAndStatus(machineId, "PENDING");
    }

    @PutMapping("/{taskId}/status")
    public Task updateTaskStatus(@PathVariable Long taskId, @RequestBody Task taskUpdated) {
        return taskRepository.findById(taskId)
                .map(existentTask-> {
                    existentTask.setStatus(taskUpdated.getStatus());

                    return taskRepository.save(existentTask);
                })
                .orElseThrow(() -> new RuntimeException("Task not Found!"));
    }
}