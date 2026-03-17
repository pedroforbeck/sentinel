package com.sentinel.api.entity;

import jakarta.persistence.*;
import jakarta.persistence.Table;
import java.time.LocalDateTime;

@Entity
@Table(name = "machine")
public class Machine {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String hostname;

    @Column(nullable = false)
    private String ipAddress;

    private String os;

    @Column(nullable = false)
    private String status; // Ex: ONLINE, OFFLINE

    private LocalDateTime lastSeen;

    // Empty constructor
    public Machine() {
    }

    // Getters e Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getHostname() { return hostname; }
    public void setHostname(String hostname) { this.hostname = hostname; }

    public String getIpAddress() { return ipAddress; }
    public void setIpAddress(String ipAddress) { this.ipAddress = ipAddress; }

    public String getOs() { return os; }
    public void setOs(String os) { this.os = os; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public LocalDateTime getLastSeen() { return lastSeen; }
    public void setLastSeen(LocalDateTime lastSeen) { this.lastSeen = lastSeen; }
}