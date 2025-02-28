package com.izikgram.alarm.controller;

import com.izikgram.alarm.service.SseEmitterService;
import com.izikgram.global.security.CustomUserDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@RestController
@CrossOrigin(origins = "http://223.130.151.184:8080") // 특정 Origin 허용
public class SseController {

    @Autowired
    private SseEmitterService sseEmitterService;

    @GetMapping(value = "/subscribe", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public ResponseEntity<SseEmitter> subscribe(@AuthenticationPrincipal CustomUserDetails userDetails) {

        String member_id = userDetails.getUser().getMember_id();

        SseEmitter sseEmitter = sseEmitterService.subscribe(member_id);

        return ResponseEntity.ok(sseEmitter);
    }

}
