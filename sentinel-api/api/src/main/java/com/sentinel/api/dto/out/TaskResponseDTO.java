package com.sentinel.api.dto.out;

import java.time.LocalDateTime;

public record TaskResponseDTO(
        Long id,
        String command,
        String status,
        String outputLog,
        LocalDateTime createdAt,
        Long machineId // only id out
) {
}