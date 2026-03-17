package com.sentinel.agent.dto;

// This record maps the JSON received from the API
public record TaskDTO(
        Long id,
        String command,
        String status
) {
}