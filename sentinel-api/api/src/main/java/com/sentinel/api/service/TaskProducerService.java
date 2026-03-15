package com.sentinel.api.service;


import com.sentinel.api.config.KafkaTopicConfig;
import com.sentinel.api.dto.in.TaskKafkaPayloadDTO;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import tools.jackson.databind.ObjectMapper;

@Service
public class TaskProducerService {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    public TaskProducerService(KafkaTemplate<String, String> kafkaTemplate, ObjectMapper objectMapper) {
        this.kafkaTemplate = kafkaTemplate;
        this.objectMapper = objectMapper;
    }

    public void sendTaskResultToQueue(Long taskId, String status, String outputLog) {
        try {
            TaskKafkaPayloadDTO payload = new TaskKafkaPayloadDTO(taskId, status, outputLog);

            // Converts the Java Object into a standard JSON String
            String jsonPayload = objectMapper.writeValueAsString(payload);

            // Fires the JSON string into the high-speed queue
            kafkaTemplate.send(KafkaTopicConfig.TASK_RESULTS_TOPIC, String.valueOf(taskId), jsonPayload);

            System.out.println("[API-KAFKA] Task " + taskId + " result successfully pushed to the processing queue.");

        } catch (Exception e) {
            // USING A GENERIC EXCEPTION TO BYPASS IDE IMPORT ISSUES
            System.err.println("[API-KAFKA-ERROR] Failed to convert payload to JSON: " + e.getMessage());
        }
    }
}