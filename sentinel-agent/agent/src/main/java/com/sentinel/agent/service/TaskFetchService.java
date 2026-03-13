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

    // Using your API's base URL. Machine ID is still hardcoded to 4 for now.
    private final String pendingUrl = "http://localhost:8080/api/tasks/machine/4/pending";
    private final String updateUrl = "http://localhost:8080/api/tasks/"; // + taskId + "/status"

    public TaskFetchService() {
        this.restTemplate = new RestTemplate();
    }

    @Scheduled(fixedRate = 10000)
    public void fetchPendingTasks() {
        System.out.println("[AGENT] Polling API for new tasks...");

        try {
            TaskDTO[] pendingTasks = restTemplate.getForObject(pendingUrl, TaskDTO[].class);

            if (pendingTasks != null && pendingTasks.length > 0) {
                System.out.println("[AGENT] Found " + pendingTasks.length + " pending task(s)!");

                for (TaskDTO task : pendingTasks) {
                    System.out.println(" -> Executing Task ID: " + task.id() + " | Command: '" + task.command() + "'");

                    // 1. Execute the command on Windows
                    String executionLog = executeWindowsCommand(task.command());

                    // 2. Send the result back to the API
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
            // "cmd.exe /c" tells Windows to run the command and terminate the shell
            ProcessBuilder builder = new ProcessBuilder("cmd.exe", "/c", command);
            builder.redirectErrorStream(true); // Merges standard errors with standard output

            Process process = builder.start();

            // Read the terminal's black screen text line by line
            // StandardCharsets.UTF_8 or "CP850" depending on your Windows language settings
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream(), StandardCharsets.UTF_8));
            String line;
            while ((line = reader.readLine()) != null) {
                output.append(line).append("\n");
            }

            process.waitFor(); // Wait for the command (like ping) to finish

        } catch (Exception e) {
            output.append("Execution failed: ").append(e.getMessage());
        }
        return output.toString();
    }

    // Method responsible for sending the PUT request back to the API
    private void reportTaskCompletion(Long taskId, String outputLog) {
        try {
            String fullUrl = updateUrl + taskId + "/status";
            TaskResultDTO resultPayload = new TaskResultDTO("COMPLETED", outputLog);

            // Send the PUT request
            restTemplate.put(fullUrl, resultPayload);
            System.out.println("[AGENT] Task " + taskId + " reported as COMPLETED back to HQ.\n");

        } catch (Exception e) {
            System.err.println("[AGENT-ERROR] Failed to report task completion: " + e.getMessage());
        }
    }
}