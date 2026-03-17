package com.sentinel.agent.service;

import com.sentinel.agent.dto.TaskDTO;
import com.sentinel.agent.dto.TaskResultDTO;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

@Service
public class TaskFetchService {

    private final RestTemplate restTemplate;
    private final RegistrationService registrationService; // Required for dynamic ID

    private final String baseUrl = "http://localhost:8080/api/tasks";

    // Constructor Injection (Spring will pass the custom RestTemplate with the API Key)
    public TaskFetchService(RestTemplate restTemplate, RegistrationService registrationService) {
        this.restTemplate = restTemplate;
        this.registrationService = registrationService;
    }

    @Scheduled(fixedRate = 10000)
    public void fetchPendingTasks() {
        // 1. Get the dynamic ID from the Registration Service
        Long myId = registrationService.getCurrentMachineId();

        if (myId == null) {
            System.out.println("[AGENT] Standing by. Awaiting valid Machine ID from HQ...");
            return;
        }

        System.out.println("[AGENT] Polling API for new tasks for Machine ID: " + myId + "...");

        try {
            // 2. Build the URL dynamically
            String pendingUrl = baseUrl + "/machine/" + myId + "/pending";
            TaskDTO[] pendingTasks = restTemplate.getForObject(pendingUrl, TaskDTO[].class);

            if (pendingTasks != null && pendingTasks.length > 0) {
                System.out.println("[AGENT] Found " + pendingTasks.length + " pending task(s)!");

                for (TaskDTO task : pendingTasks) {
                    System.out.println(" -> Executing Task ID: " + task.id() + " | Command: '" + task.command() + "'");

                    String executionLog = executeWindowsCommand(task.command());
                    reportTaskCompletion(task.id(), executionLog);
                }
            } else {
                System.out.println("[AGENT] No pending tasks. Standing by.");
            }

        } catch (Exception e) {
            System.err.println("[AGENT-ERROR] Connection to Command Center failed: " + e.getMessage());
        }
    }

    // Method responsible for opening the terminal and grabbing the output
    private String executeWindowsCommand(String command) {
        StringBuilder output = new StringBuilder();
        try {
            ProcessBuilder builder = new ProcessBuilder("cmd.exe", "/c", command);
            builder.redirectErrorStream(true);

            Process process = builder.start();

            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream(), StandardCharsets.UTF_8));
            String line;
            while ((line = reader.readLine()) != null) {
                output.append(line).append("\n");
            }

            process.waitFor();

        } catch (Exception e) {
            output.append("Execution failed: ").append(e.getMessage());
        }
        return output.toString();
    }

    // Method responsible for sending the PUT request back to the API
    private void reportTaskCompletion(Long taskId, String outputLog) {
        try {
            String fullUrl = baseUrl + "/" + taskId + "/status";
            TaskResultDTO resultPayload = new TaskResultDTO("COMPLETED", outputLog);

            restTemplate.put(fullUrl, resultPayload);
            System.out.println("[AGENT] Task " + taskId + " reported as COMPLETED back to HQ.\n");

        } catch (Exception e) {
            System.err.println("[AGENT-ERROR] Failed to report task completion: " + e.getMessage());
        }
    }
}