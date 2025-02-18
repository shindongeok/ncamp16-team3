package com.izikgram.user.controller;

import com.izikgram.user.entity.User;
import com.izikgram.user.service.AuthService;
import com.izikgram.user.service.UserService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @Autowired
    UserService userService;

    @PostMapping("/send-sms")
    public ResponseEntity<String> sendSms(@RequestParam String phone_num) {
//        log.info("컨트롤러는 들어오니??");
//        log.info("phone_num: {}", phone_num);
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
    public String verifyCode(@RequestParam String name, @RequestParam String phone_num, @RequestParam String auth_num, HttpSession session) {
        User user = userService.findUserFromFindId(name, phone_num);
        return isUserExist(phone_num, auth_num, session, user);
    }

    @PostMapping("/verifyPw")
    public String verifyPwCode(@RequestParam String member_id, @RequestParam String phone_num, @RequestParam String auth_num, HttpSession session) {
        User user = userService.findUserFromFindPw(member_id, phone_num);
        return isUserExist(phone_num, auth_num, session, user);
    }

    private @NotNull String isUserExist(String phone_num, String auth_num, HttpSession session, User user) {
        if (user != null) {
            if (authService.verifyCode(phone_num, auth_num)) {
//            log.info("/vefiry -> name: {}, phone_num: {}, code: {}", name, phone_num, auth_num);
                String memberId = user.getMember_id();
                session.setAttribute("member_id", memberId);
                return "success";
            } else {
                return "fail";
            }
        } else {
            return "null";
        }
    }
}
