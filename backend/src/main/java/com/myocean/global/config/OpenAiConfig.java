package com.myocean.global.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class OpenAiConfig {

    @Value("${openai.api.key:your-openai-api-key}")
    private String apiKey;

    @Value("${openai.api.url:https://gms.ssafy.io/gmsapi/api.openai.com/v1}")
    private String apiUrl;

    @Value("${openai.model:gpt-4o}")
    private String model;

    @Value("${openai.models.light:gpt-4o-mini}")
    private String lightModel;

    @Value("${openai.models.mini:gpt-4o-mini}")
    private String miniModel;

    @Value("${openai.models.standard:gpt-4o}")
    private String standardModel;

    @Bean
    public RestTemplate openAiRestTemplate() {
        return new RestTemplate();
    }

    public String getApiKey() {
        return apiKey;
    }

    public String getApiUrl() {
        return apiUrl;
    }

    public String getModel() {
        return model;
    }

    public String getLightModel() {
        return lightModel;
    }

    public String getMiniModel() {
        return miniModel;
    }

    public String getStandardModel() {
        return standardModel;
    }
}