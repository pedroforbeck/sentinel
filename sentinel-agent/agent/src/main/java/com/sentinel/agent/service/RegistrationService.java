package com.sentinel.agent.service;

import com.sentinel.agent.dto.MachineRegistrationDTO;
import com.sentinel.agent.dto.MachineResponseDTO;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.net.InetAddress;
import java.net.UnknownHostException;

@Service
public class RegistrationService {

    private final RestTemplate restTemplate;
    private final String registerUrl = "http://localhost:8080/api/machines/register";

    // This will hold the official ID given by the HQ
    private Long currentMachineId = null;

    public RegistrationService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate; // Spring will inject the configured one
    }

    @PostConstruct
    public void registerAgent() {
        System.out.println("[AGENT-INIT] Starting auto-registration sequence...");

        try {
            // 1. Discover the host machine's actual name and OS
            String hostname = InetAddress.getLocalHost().getHostName();
            String osName = System.getProperty("os.name");

            System.out.println("[AGENT-INIT] Detected Hostname: " + hostname);
            System.out.println("[AGENT-INIT] Detected OS: " + osName);

            // 2. Prepare the payload
            MachineRegistrationDTO payload = new MachineRegistrationDTO(hostname, osName);

            // 3. Send to Headquarters
            MachineResponseDTO response = restTemplate.postForObject(registerUrl, payload, MachineResponseDTO.class);

            if (response != null && response.id() != null) {
                this.currentMachineId = response.id();
                System.out.println("[AGENT-INIT] Registration successful! My official Machine ID is: " + this.currentMachineId + "\n");
            }

        } catch (UnknownHostException e) {
            System.err.println("[AGENT-ERROR] Could not determine local hostname.");
        } catch (Exception e) {
            System.err.println("[AGENT-ERROR] Registration failed. HQ might be offline: " + e.getMessage());
        }
    }

    // Getter so the TaskFetchService can ask for the ID
    public Long getCurrentMachineId() {
        return currentMachineId;
    }
}