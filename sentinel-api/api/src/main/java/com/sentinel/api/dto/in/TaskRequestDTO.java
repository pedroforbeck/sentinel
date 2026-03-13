package com.sentinel.api.dto.in;

public record TaskRequestDTO(
        String command,
        String status,
        String outputLog
) {
}