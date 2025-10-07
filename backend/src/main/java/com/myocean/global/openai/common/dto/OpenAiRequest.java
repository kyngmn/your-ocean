package com.myocean.global.openai.common.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@AllArgsConstructor
@Getter
public class OpenAiRequest {
    private String model;
    private List<Message> messages;
    private Double temperature;

    public static OpenAiRequest create(String model, List<Message> messages) {
        return new OpenAiRequest(model, messages, 0.7);
    }

    public static OpenAiRequest createJsonMode(String model, List<Message> messages) {
        return new OpenAiRequest(model, messages, 0.7);
    }

    @AllArgsConstructor
    @Getter
    public static class Message {
        private String role;
        private String content;

        public static Message system(String content) {
            return new Message("system", content);
        }

        public static Message user(String content) {
            return new Message("user", content);
        }

        public static Message assistant(String content) {
            return new Message("assistant", content);
        }
    }
}