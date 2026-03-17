package com.sentinel.agent.dto;

public record MachineResponseDTO(
        Long id,
        String hostname,
        String ipAddress,
        String os,
        String status,
        String lastSeen
) {
}