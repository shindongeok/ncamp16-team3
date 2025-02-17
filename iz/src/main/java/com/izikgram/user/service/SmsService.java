package com.izikgram.user.service;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import net.nurigo.sdk.NurigoApp;
import net.nurigo.sdk.message.model.Message;
import net.nurigo.sdk.message.service.DefaultMessageService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;

@Slf4j
@Service
public class SmsService {

    private DefaultMessageService messageService;

    @Value("${coolsms.api-key}")
    private String apiKey;

    @Value("${coolsms.api-secret}")
    private String apiSecret;

    @Value("${coolsms.sender-number}")
    private String senderNumber;

    @Value("${coolsms.domain}")
    private String domain;


    @PostConstruct
    private void init() {
        // 값이 올바르게 주입되었는지 확인
        if (apiKey == null || apiSecret == null || domain == null) {
            log.error("CoolSMS API 설정값이 존재하지 않습니다. apiKey: {}, apiSecret: {}, domain: {}", apiKey, apiSecret, domain);
            throw new IllegalStateException("CoolSMS API 설정이 올바르지 않습니다.");
        }
        this.messageService = NurigoApp.INSTANCE.initialize(apiKey, apiSecret, domain);
    }

    public String sendVerificationCode(String to) {
        String code = generateCode();
        log.info("code: {}", code);

        Message message = new Message();
        message.setFrom(senderNumber);
        message.setTo(to);
        message.setText("[인증번호] " + code + " (3분 이내 입력)");

        try {
            messageService.send(message);
            log.info("message: {}", message);
            log.info("인증번호 {}가 {}로 전송되었습니다.", code, to);
        } catch (Exception e) {
            log.error("SMS 전송 실패: {}", e.getMessage(), e);
            throw new RuntimeException("SMS 전송 중 오류가 발생했습니다.");
        }

        return code;
    }

    private String generateCode() {
        SecureRandom random = new SecureRandom();
        int code = 100000 + random.nextInt(900000);
        return String.valueOf(code);
    }

}
