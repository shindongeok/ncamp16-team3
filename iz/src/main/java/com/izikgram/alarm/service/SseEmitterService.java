package com.izikgram.alarm.service;

import com.izikgram.alarm.repository.SseEmitterRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;

@Service
public class SseEmitterService {

    @Autowired
    private SseEmitterRepository sseEmitterRepository;

    public SseEmitter subscribe(String member_id, String lastEventId) {

        // 1. 현재 클라이언트를 위한 sseEmitter 객체 생성
        SseEmitter sseEmitter = new SseEmitter(Long.MAX_VALUE);

        // 2. 연결
        try {
            sseEmitter.send(SseEmitter.event().name("connect"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        // 3. 저장
        sseEmitterRepository.put(member_id, sseEmitter);

        // 4. 연결 종료 처리
        sseEmitter.onCompletion(() -> sseEmitterRepository.remove(member_id));
        sseEmitter.onError((e) -> sseEmitterRepository.remove(member_id));
        sseEmitter.onTimeout(() -> sseEmitterRepository.remove(member_id));

        return sseEmitter;
    }

    public void send(String member_id, String content) {
        // 2️⃣ 작성자가 SSE에 연결되어 있으면 알림 전송
        if (sseEmitterRepository.containsKey(member_id)) {
            SseEmitter sseEmitter = sseEmitterRepository.findByMemberId(member_id);
            try {
                // 3️⃣ SSE를 통해 "새로운 댓글이 달렸습니다!" 이벤트 전송
                sseEmitter.send(SseEmitter.event().name("message").data(content));
            } catch (IOException e) {
                // 4️⃣ SSE 연결이 끊어졌다면 해당 사용자 제거
                sseEmitterRepository.remove(member_id);
            }
        }
    }

    public void popularSend(String member_id, String content) {
        // 2️⃣ 작성자가 SSE에 연결되어 있으면 알림 전송
        if (sseEmitterRepository.containsKey(member_id)) {
            SseEmitter sseEmitter = sseEmitterRepository.findByMemberId(member_id);
            try {
                // 3️⃣ SSE를 통해 "새로운 댓글이 달렸습니다!" 이벤트 전송
                sseEmitter.send(SseEmitter.event().name("popular-message").data(content));
            } catch (IOException e) {
                // 4️⃣ SSE 연결이 끊어졌다면 해당 사용자 제거
                sseEmitterRepository.remove(member_id);
            }
        }
    }


}
