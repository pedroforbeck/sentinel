package com.sentinel.api.repository;

import com.sentinel.api.model.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {
    List<Task> findByMachineIdAndStatus(Long machineId, String status);
}