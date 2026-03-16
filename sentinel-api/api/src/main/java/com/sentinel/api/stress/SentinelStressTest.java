package com.sentinel.api.stress;

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
        System.out.println("🚀 Initiating Stress Test on Sentinel HQ...");
        System.out.println("Target: " + TOTAL_REQUESTS + " requests using " + CONCURRENT_THREADS + " concurrent threads.\n");

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
                    // 1. SHOT 1: Create the Task (Stresses the Database)
                    String createPayload = "{\"command\": \"echo stress_test_" + requestNumber + "\"}";
                    HttpRequest createReq = HttpRequest.newBuilder()
                            .uri(URI.create(API_URL + "/machine/" + MACHINE_ID))
                            .header("Content-Type", "application/json")
                            .header("X-API-KEY", API_KEY)
                            .POST(HttpRequest.BodyPublishers.ofString(createPayload))
                            .build();

                    HttpResponse<String> createRes = client.send(createReq, HttpResponse.BodyHandlers.ofString());

                    if (createRes.statusCode() == 200) {
                        // Extract the created task ID using a simple regex
                        String responseBody = createRes.body();
                        String taskId = responseBody.replaceAll(".*\"id\":\\s*(\\d+).*", "$1");

                        // 2. SHOT 2: Complete the Task (Stresses Kafka and the Consumer)
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

        // Wait for all threads to finish firing
        executor.shutdown();
        executor.awaitTermination(1, TimeUnit.MINUTES);

        long endTime = System.currentTimeMillis();
        long totalTimeMs = endTime - startTime;

        System.out.println("\n🏁 TEST COMPLETE!");
        System.out.println("⏱️ Total time: " + totalTimeMs + " ms");
        System.out.println("✅ Successes: " + successCount.get());
        System.out.println("❌ Failures: " + errorCount.get());
        System.out.println("⚡ Throughput: " + (TOTAL_REQUESTS * 1000L / Math.max(totalTimeMs, 1)) + " requests per second");
    }
}
