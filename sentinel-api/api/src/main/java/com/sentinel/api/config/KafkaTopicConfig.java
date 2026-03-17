package com.sentinel.api.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaTopicConfig {

    public static final String TASK_RESULTS_TOPIC = "task-results";

    @Bean
    public NewTopic taskResultsTopic() {
        return TopicBuilder.name(TASK_RESULTS_TOPIC)
                .partitions(3) // Allows up to 3 consumers reading in parallel later
                .replicas(1) // Since only 1 Kafka broker is in Docker
                .build();
    }
}