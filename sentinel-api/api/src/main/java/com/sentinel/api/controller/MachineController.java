package com.sentinel.api.controller;


import com.sentinel.api.dto.in.MachineRegistrationDTO;
import com.sentinel.api.dto.out.MachineResponseDTO;
import com.sentinel.api.model.Machine;
import com.sentinel.api.repository.MachineRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/machines")
public class MachineController {

    private final MachineRepository machineRepository;

    public MachineController(MachineRepository machineRepository) {
        this.machineRepository = machineRepository;
    }

    // AGENT ENDPOINT: DYNAMIC REGISTRATION
    @PostMapping("/register")
    public MachineResponseDTO registerMachine(@RequestBody MachineRegistrationDTO dtoIn, HttpServletRequest request) {

        // Magically extracts the IP address from the incoming network packet
        String extractedIp = request.getRemoteAddr();

        // Checks if the machine already exists in the database
        Machine machine = machineRepository.findByHostname(dtoIn.hostname())
                .orElseGet(() -> {
                    // If it doesn't exist, create a new blank machine
                    Machine newMachine = new Machine();
                    newMachine.setHostname(dtoIn.hostname());
                    return newMachine;
                });

        // Update the data (whether it's new or existing, we refresh the info)
        machine.setOs(dtoIn.os());
        machine.setIpAddress(extractedIp);
        machine.setStatus("ONLINE");
        machine.setLastSeen(LocalDateTime.now());

        // Save to the database
        Machine savedMachine = machineRepository.save(machine);

        // Return the full DTO to the Agent (so it knows its official ID)
        return new MachineResponseDTO(
                savedMachine.getId(),
                savedMachine.getHostname(),
                savedMachine.getIpAddress(),
                savedMachine.getOs(),
                savedMachine.getStatus(),
                savedMachine.getLastSeen()
        );
    }

    // STANDARD CRUD ENDPOINTS (For Admin Panel)
    @PostMapping
    public Machine createMachine(@RequestBody Machine machine) {
        return machineRepository.save(machine);
    }

    @GetMapping
    public List<Machine> gettAllMachines() {
        return machineRepository.findAll();
    }

    @GetMapping("/{id}")
    public Machine getMachineById(@PathVariable Long id) {
        return machineRepository.findById(id).orElse(null);
    }

    @PutMapping("/{id}")
    public Machine updateMachine(@PathVariable Long id, @RequestBody Machine updatedMachine) {
        return machineRepository.findById(id)
                .map(existingMachine -> {
                    existingMachine.setHostname(updatedMachine.getHostname());
                    existingMachine.setIpAddress(updatedMachine.getIpAddress());
                    existingMachine.setOs(updatedMachine.getOs());
                    existingMachine.setStatus(updatedMachine.getStatus());
                    existingMachine.setLastSeen(updatedMachine.getLastSeen());

                    return machineRepository.save(existingMachine);
                })
                .orElse(null);
    }

    @DeleteMapping("/{id}")
    public void deleteMachine(@PathVariable Long id) {
        machineRepository.deleteById(id);
    }
}