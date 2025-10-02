package com.myocean;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@Tag(name = "Hello", description = "Hello API")
@RestController
@CrossOrigin(originPatterns = "*", allowedHeaders = "*", allowCredentials = "false")
public class HelloController {

    @Operation(summary = "기본 인사", description = "기본 Hello World 메시지를 반환합니다.")
    @GetMapping("/")
    public String hello() {
        return "Webhook Test Success!";
    }

    @Operation(summary = "MyOcean 인사", description = "MyOcean 전용 인사 메시지를 반환합니다.")
    @GetMapping("/hello")
    public ResponseEntity<String> helloPath() {
        return ResponseEntity.ok()
                .header("Access-Control-Allow-Origin", "*")
                .header("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS")
                .header("Access-Control-Allow-Headers", "*")
                .body("Hello from MyOcean!");
    }

    @GetMapping("/health")
    public ResponseEntity<Map<String, Object>> health() {
        Map<String, Object> healthStatus = new HashMap<>();

        try {
            healthStatus.put("status", "UP");
            healthStatus.put("timestamp", Instant.now().toString());
            healthStatus.put("service", "MyOcean Backend");
            healthStatus.put("version", "0.0.1-SNAPSHOT");

            Map<String, Object> details = new HashMap<>();
            details.put("database", "UP");
            details.put("memory", getMemoryInfo());
            details.put("uptime", getUptimeInfo());

            healthStatus.put("components", details);

            return ResponseEntity.ok(healthStatus);

        } catch (Exception e) {
            healthStatus.put("status", "DOWN");
            healthStatus.put("error", e.getMessage());
            healthStatus.put("timestamp", Instant.now().toString());

            return ResponseEntity.status(503).body(healthStatus);
        }
    }

    @GetMapping("/health/liveness")
    public ResponseEntity<Map<String, String>> liveness() {
        Map<String, String> status = new HashMap<>();
        status.put("status", "UP");
        status.put("check", "liveness");
        status.put("timestamp", Instant.now().toString());
        return ResponseEntity.ok(status);
    }

    @GetMapping("/health/readiness")
    public ResponseEntity<Map<String, String>> readiness() {
        Map<String, String> status = new HashMap<>();

        boolean isReady = checkReadiness();

        if (isReady) {
            status.put("status", "UP");
            status.put("check", "readiness");
            status.put("timestamp", Instant.now().toString());
            return ResponseEntity.ok(status);
        } else {
            status.put("status", "DOWN");
            status.put("check", "readiness");
            status.put("timestamp", Instant.now().toString());
            return ResponseEntity.status(503).body(status);
        }
    }

    private Map<String, String> getMemoryInfo() {
        Runtime runtime = Runtime.getRuntime();
        long maxMemory = runtime.maxMemory();
        long totalMemory = runtime.totalMemory();
        long freeMemory = runtime.freeMemory();
        long usedMemory = totalMemory - freeMemory;

        Map<String, String> memoryInfo = new HashMap<>();
        memoryInfo.put("max", formatBytes(maxMemory));
        memoryInfo.put("total", formatBytes(totalMemory));
        memoryInfo.put("used", formatBytes(usedMemory));
        memoryInfo.put("free", formatBytes(freeMemory));

        return memoryInfo;
    }

    private String getUptimeInfo() {
        long uptimeMillis = System.currentTimeMillis() -
            java.lang.management.ManagementFactory.getRuntimeMXBean().getStartTime();
        long uptimeSeconds = uptimeMillis / 1000;
        long hours = uptimeSeconds / 3600;
        long minutes = (uptimeSeconds % 3600) / 60;
        long seconds = uptimeSeconds % 60;

        return String.format("%d시간 %d분 %d초", hours, minutes, seconds);
    }

    private String formatBytes(long bytes) {
        if (bytes < 1024) return bytes + " B";
        int exp = (int) (Math.log(bytes) / Math.log(1024));
        String pre = "KMGTPE".charAt(exp - 1) + "";
        return String.format("%.1f %sB", bytes / Math.pow(1024, exp), pre);
    }

    private boolean checkReadiness() {
        return true;
    }
}