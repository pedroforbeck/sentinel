package com.sentinel.api.dto.out;

import java.time.LocalDateTime;

public record MachineResponseDTO(
        Long id,
        String hostname,
        String ipAddress,
        String os,
        String status,
        LocalDateTime lastSeen
) {
}