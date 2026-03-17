package com.sentinel.api.dto.in;

public record TaskKafkaPayloadDTO(
        Long taskId,
        String status,
        String outputLog
) {
}