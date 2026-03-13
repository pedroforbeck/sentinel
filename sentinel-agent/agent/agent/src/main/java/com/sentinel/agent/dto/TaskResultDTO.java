package com.sentinel.agent.dto;

// This record maps the JSON the Agent will send via PUT to update the task
public record TaskResultDTO(
        String status,
        String outputLog
) {
}