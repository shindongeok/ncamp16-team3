package com.izikgram.alarm.repository;

import org.springframework.stereotype.Repository;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class SseEmitterRepository {

    private static Map<String, SseEmitter> sseEmitters = new ConcurrentHashMap<>();

    public void put(String member_id, SseEmitter sseEmitter) {
        sseEmitters.put(member_id, sseEmitter);
    }

    public void remove(String member_id) {
        sseEmitters.remove(member_id);
    }

    public boolean containsKey(String member_id) {
        if(sseEmitters.containsKey(member_id)) {
            return true;
        }
        return false;
    }

    public SseEmitter findByMemberId(String member_id) {
        return sseEmitters.get(member_id);
    }

}
