package com.sentinel.api.controller;


import com.sentinel.api.model.Machine;
import com.sentinel.api.repository.MachineRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/machines")
public class MachineController {

    private final MachineRepository machineRepository;


    public MachineController(MachineRepository machineRepository) {
        this.machineRepository = machineRepository;
    }

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
