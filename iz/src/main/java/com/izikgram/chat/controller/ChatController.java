package com.izikgram.chat.controller;

import com.izikgram.chat.dto.ChatRequest;
import com.izikgram.chat.dto.ChatResponse;
import com.izikgram.chat.service.ChatService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/chat")
@RequiredArgsConstructor
public class ChatController {

    private final ChatService chatService;

    @PostMapping("/ask")
    public ResponseEntity<ChatResponse> analyzeSentiment(@RequestBody ChatRequest request) {
        ChatResponse response = chatService.getSentimentAnalysis(request);
        log.info("request={}" , request);
        log.info("response={}" , response);
        return ResponseEntity.ok(response);

    }


}
