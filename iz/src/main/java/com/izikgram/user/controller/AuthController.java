package com.izikgram.user.controller;

import com.izikgram.user.service.AuthService;
import com.izikgram.user.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/send-sms")
    public ResponseEntity<String> sendSms(@RequestParam String phone_num) {
        log.info("컨트롤러는 들어오니??");
        log.info("phone_num: {}", phone_num);
//        authService.sendVerificationCode(phone_num);
        return ResponseEntity.ok("인증번호가 전송되었습니다.");
    }

//    @PostMapping("/verify")
//    public ResponseEntity<String> verifyCode(@RequestParam String phone_num, @RequestParam String auth_num) {
//        log.info("phone_num: {}, code: {}", phone_num, auth_num);
//        if (authService.verifyCode(phone_num, auth_num)) {
//            return ResponseEntity.ok("인증 성공");
//        } else {
//            return ResponseEntity.badRequest().body("인증 실패");
//        }
//    }

    @PostMapping("/verify")
    public String verifyCode(@RequestParam String phone_num, @RequestParam String auth_num) {
        log.info("phone_num: {}, code: {}", phone_num, auth_num);
        if (authService.verifyCode(phone_num, auth_num)) {
            return phone_num;
        } else {
            return "실패";
        }
    }
}
