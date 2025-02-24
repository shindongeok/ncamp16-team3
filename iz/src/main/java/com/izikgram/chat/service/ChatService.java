package com.izikgram.chat.service;

import com.izikgram.chat.config.ChatConfig;
import com.izikgram.chat.dto.ChatRequest;
import com.izikgram.chat.dto.ChatResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class ChatService {

    private final ChatConfig chatConfig;
    private final RestTemplate restTemplate;

    public ChatResponse getSentimentAnalysis(ChatRequest request) {
        // 요청 헤더 설정
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " + chatConfig.getApiKey()); // API 키 설정
        headers.set("X-NCP-CLOVASTUDIO-REQUEST-ID", "290fee0cb7214ea19a9c1d8ce509bd51"); // 고유 요청 ID 설정
        headers.set("Accept", "application/json");

        HttpEntity<ChatRequest> entity = new HttpEntity<>(request, headers);
        ResponseEntity<Map> response = restTemplate.exchange(
                chatConfig.getApiUrl(),
                HttpMethod.POST,
                entity,
                Map.class
        );

        Map<String, Object> responseBody = response.getBody();
        if (responseBody == null || !responseBody.containsKey("message")) {
            return new ChatResponse("분석 실패", 0);
        }

        // ClovaX 응답에서 message.content 값 가져오기
        Map<String, String> message = (Map<String, String>) responseBody.get("message");
        String content = message.get("content");

        // ChatResponse 객체 생성
        return ChatResponse.fromApiResponse(content);
    }

}
