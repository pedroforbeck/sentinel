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

    private static final String API_URL = "http://localhost:8080/api/tasks";
    private static final String API_KEY = "7b2c8a9f-3e4d-4a1b-9c8d-7e6f5a4b3c2d";
    private static final int MACHINE_ID = 1;

    private static final int REQUESTS_PER_ROUND = 1000;
    private static final int CONCURRENT_THREADS = 100;

    public static void main(String[] args) throws InterruptedException {
        System.out.println("🚀 Starting NONSTOP Stress Test on Sentinel HQ...");
        System.out.println("Press Ctrl+C to stop.\n");

        HttpClient client = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(10))
                .build();

        AtomicInteger totalSuccess = new AtomicInteger(0);
        AtomicInteger totalErrors = new AtomicInteger(0);
        int round = 0;

        while (true) {
            round++;
            AtomicInteger successCount = new AtomicInteger(0);
            AtomicInteger errorCount = new AtomicInteger(0);

            ExecutorService executor = Executors.newFixedThreadPool(CONCURRENT_THREADS);
            long startTime = System.currentTimeMillis();

            for (int i = 0; i < REQUESTS_PER_ROUND; i++) {
                final int requestNumber = (round * REQUESTS_PER_ROUND) + i;
                executor.submit(() -> {
                    try {
                        String createPayload = "{\"command\": \"echo stress_test_" + requestNumber + "\"}";
                        HttpRequest createReq = HttpRequest.newBuilder()
                                .uri(URI.create(API_URL + "/machine/" + MACHINE_ID))
                                .header("Content-Type", "application/json")
                                .header("X-API-KEY", API_KEY)
                                .POST(HttpRequest.BodyPublishers.ofString(createPayload))
                                .build();

                        HttpResponse<String> createRes = client.send(createReq, HttpResponse.BodyHandlers.ofString());

                        if (createRes.statusCode() == 200) {
                            String taskId = createRes.body().replaceAll(".*\"id\":\\s*(\\d+).*", "$1");

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
                                totalSuccess.incrementAndGet();
                            } else {
                                errorCount.incrementAndGet();
                                totalErrors.incrementAndGet();
                            }
                        } else {
                            errorCount.incrementAndGet();
                            totalErrors.incrementAndGet();
                        }

                    } catch (Exception e) {
                        errorCount.incrementAndGet();
                        totalErrors.incrementAndGet();
                    }
                });
            }

            executor.shutdown();
            executor.awaitTermination(2, TimeUnit.MINUTES);

            long elapsed = System.currentTimeMillis() - startTime;
            long rps = REQUESTS_PER_ROUND * 1000L / Math.max(elapsed, 1);

            System.out.printf("🔁 Round %d | ✅ %d | ❌ %d | ⚡ %d req/s | 📊 Total: %d success / %d errors%n",
                    round, successCount.get(), errorCount.get(), rps,
                    totalSuccess.get(), totalErrors.get());
        }
    }
}