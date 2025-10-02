package com.myocean.global.config;

import io.github.cdimascio.dotenv.Dotenv;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;
import org.springframework.core.env.PropertySource;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
@Slf4j
public class DotenvConfig implements EnvironmentPostProcessor {

    @Override
    public void postProcessEnvironment(ConfigurableEnvironment environment, SpringApplication application) {
        try {
            Dotenv dotenv = Dotenv.configure()
                    .directory("./")
                    .ignoreIfMalformed()
                    .ignoreIfMissing()
                    .load();

            Map<String, Object> dotenvMap = new HashMap<>();
            dotenv.entries().forEach(entry -> {
                dotenvMap.put(entry.getKey(), entry.getValue());
                // System properties도 설정 (기존 방식 유지)
                System.setProperty(entry.getKey(), entry.getValue());
            });

            PropertySource<Map<String, Object>> dotenvPropertySource = new MapPropertySource("dotenv", dotenvMap);
            environment.getPropertySources().addFirst(dotenvPropertySource);

            log.info("Loaded .env file with {} properties", dotenvMap.size());

        } catch (Exception e) {
            log.warn("Warning: .env file not found or could not be loaded: {}", e.getMessage());
        }
    }
}