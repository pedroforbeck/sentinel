package com.sentinel.api;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class SentinelStressTest {

    // Target configuration
    private static final String API_URL = "http://localhost:8080/api/tasks";
    private static final String API_KEY = "7b2c8a9f-3e4d-4a1b-9c8d-7e6f5a4b3c2d";
    private static final int MACHINE_ID = 6; // Your registered agent ID

    // Artillery configuration
    private static final int TOTAL_REQUESTS = 1000;
    private static final int CONCURRENT_THREADS = 100;

    public static void main(String[] args) throws InterruptedException {
        System.out.println("🚀 Iniciando Teste de Stress no Sentinel HQ...");
        System.out.println("Alvo: " + TOTAL_REQUESTS + " requisições usando " + CONCURRENT_THREADS + " threads simultâneas.\n");

        HttpClient client = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(10))
                .build();

        ExecutorService executor = Executors.newFixedThreadPool(CONCURRENT_THREADS);

        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger errorCount = new AtomicInteger(0);

        long startTime = System.currentTimeMillis();

        for (int i = 0; i < TOTAL_REQUESTS; i++) {
            final int requestNumber = i;
            executor.submit(() -> {
                try {
                    // 1. DISPARO 1: Cria a Tarefa (Força o Banco de Dados)
                    String createPayload = "{\"command\": \"echo stress_test_" + requestNumber + "\"}";
                    HttpRequest createReq = HttpRequest.newBuilder()
                            .uri(URI.create(API_URL + "/machine/" + MACHINE_ID))
                            .header("Content-Type", "application/json")
                            .header("X-API-KEY", API_KEY)
                            .POST(HttpRequest.BodyPublishers.ofString(createPayload))
                            .build();

                    HttpResponse<String> createRes = client.send(createReq, HttpResponse.BodyHandlers.ofString());

                    if (createRes.statusCode() == 200) {
                        // Extrai o ID da tarefa criada usando uma regex simples
                        String responseBody = createRes.body();
                        String taskId = responseBody.replaceAll(".*\"id\":\\s*(\\d+).*", "$1");

                        // 2. DISPARO 2: Responde a Tarefa (Força o Kafka e o Consumer)
                        String updatePayload = "{\"status\": \"COMPLETED\", \"outputLog\": \"Stress test payload delivered.\"}";
                        HttpRequest updateReq = HttpRequest.newBuilder()
                                .uri(URI.create(API_URL + "/" + taskId + "/status"))
                                .header("Content-Type", "application/json")
                                .header("X-API-KEY", API_KEY)
                                .PUT(HttpRequest.BodyPublishers.ofString(updatePayload))
                                .build();

                        HttpResponse<String> updateRes = client.send(updateReq, HttpResponse.BodyHandlers.ofString());

                        if (updateRes.statusCode() == 202) {
                            successCount.incrementAndGet();
                        } else {
                            errorCount.incrementAndGet();
                        }
                    } else {
                        errorCount.incrementAndGet();
                    }

                } catch (Exception e) {
                    errorCount.incrementAndGet();
                }
            });
        }

        // Aguarda todas as threads terminarem os disparos
        executor.shutdown();
        executor.awaitTermination(1, TimeUnit.MINUTES);

        long endTime = System.currentTimeMillis();
        long totalTimeMs = endTime - startTime;

        System.out.println("\n🏁 TESTE CONCLUÍDO!");
        System.out.println("⏱️ Tempo total: " + totalTimeMs + " ms");
        System.out.println("✅ Sucessos: " + successCount.get());
        System.out.println("❌ Falhas: " + errorCount.get());
        System.out.println("⚡ Taxa de transferência: " + (TOTAL_REQUESTS * 1000L / totalTimeMs) + " missões completas por segundo");
    }
}