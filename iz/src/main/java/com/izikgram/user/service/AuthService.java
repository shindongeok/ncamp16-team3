package com.izikgram.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final SmsService smsService;
    private final StringRedisTemplate redisTemplate;

    public void sendVerificationCode(String phone_num) {
        String code = smsService.sendVerificationCode(phone_num);
        redisTemplate.opsForValue().set(phone_num, code, Duration.ofMinutes(3)); // 3분 동안 유효
    }

    public boolean verifyCode(String phone_num, String code) {
        String storedCode = redisTemplate.opsForValue().get(phone_num);
        return storedCode != null && storedCode.equals(code);
    }
}

