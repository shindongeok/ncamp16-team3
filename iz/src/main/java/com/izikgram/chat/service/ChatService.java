package com.izikgram.chat.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.izikgram.chat.config.ChatConfig;
import com.izikgram.chat.dto.Message;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Service
public class ChatService {

    private static final Logger logger = LoggerFactory.getLogger(ChatService.class);
    private final ChatConfig chatConfig;
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;


    @Autowired
    public ChatService(ChatConfig chatConfig, RestTemplate restTemplate, ObjectMapper objectMapper) {
        this.chatConfig = chatConfig;
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
    }

    public String sendMessageToClova(List<Message> messages) {
        String apiUrl = chatConfig.getApiUrl();
        String apiKey = chatConfig.getApiKey();

//        logger.debug("📌 Clova API URL: {}", apiUrl);
//        logger.debug("📌 Clova API Key: {}", apiKey);
//        logger.debug("📌 Clova API 요청 본문: {}", messages.toString());

        // Clova API 요청 본문 구성
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("messages", messages);

        // 필요한 설정 추가
        Map<String, Object> config = new HashMap<>();
        config.put("temperature", 0.7);  // 적절한 값으로 조정
        config.put("maxTokens", 800);    // 적절한 값으로 조정
        requestBody.put("config", config);

        // 모델 ID 설정
        requestBody.put("modelId", "HyperCLOVA X");

        // Clova API 호출을 위한 헤더 설정 (Postman 설정과 일치하도록 수정)
        HttpHeaders headers = new HttpHeaders();
        // Authorization 베어러 토큰 설정
        headers.set("Authorization", "Bearer " + apiKey);

        // 요청 ID 설정 (필요시 UUID로 생성)
        String requestId = UUID.randomUUID().toString().replace("-", "");
        headers.set("X-NCP-CLOVASTUDIO-REQUEST-ID", requestId);

        // Content-Type 및 Accept 헤더 설정
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Accept", "text/event-stream");  // 스트리밍 응답을 받기 위한 설정

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

        log.info("ChatService 요청 정보: {}", entity);

        try {
            ResponseEntity<String> response = restTemplate.exchange(apiUrl, HttpMethod.POST, entity, String.class);
            String responseBody = response.getBody();
            logger.info("Clova API 응답: {}", response.getBody());
            String content = extractContentFromStreamResponse(responseBody);
            logger.info("추출된 content: {}", content);

            return content;
        } catch (HttpClientErrorException e) {
            logger.error("Clova API 오류 응답: {}", e.getResponseBodyAsString());
            throw new RuntimeException("Clova API 호출 중 오류 발생: " + e.getResponseBodyAsString(), e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private String extractContentFromStreamResponse(String responseBody) throws IOException {
        // event:result 부분을 찾아서 해당 data 라인의 JSON에서 content 추출
        BufferedReader reader = new BufferedReader(new StringReader(responseBody));
        String line;
        String dataLine = null;

        while ((line = reader.readLine()) != null) {
            if (line.startsWith("event:result")) {
                // 다음 라인이 data 라인일 것으로 예상
                dataLine = reader.readLine();
                break;
            }
        }

        if (dataLine != null && dataLine.startsWith("data:")) {
            // data: 접두사 제거하고 JSON 파싱
            String jsonStr = dataLine.substring(5);
            JsonNode rootNode = objectMapper.readTree(jsonStr);

            if (rootNode.has("message") && rootNode.get("message").has("content")) {
                return rootNode.get("message").get("content").asText();
            }
        }

        // content를 찾지 못한 경우 원본 응답 반환
        logger.warn("응답에서 content를 찾을 수 없습니다");
        return responseBody;
    }
}