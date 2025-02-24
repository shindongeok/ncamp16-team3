package com.izikgram.chat.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ChatConfig {

    @Value("${clova.api.url}")
    private String apiUrl;

    @Value("${clova.api.key}")
    private String apiKey;

    public String getApiUrl() {
        return apiUrl;
    }

    public String getApiKey() {
        return apiKey;
    }
}
