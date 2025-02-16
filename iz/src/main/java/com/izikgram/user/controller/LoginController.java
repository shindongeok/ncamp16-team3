package com.izikgram.user.controller;

import com.izikgram.user.entity.User;
import com.izikgram.user.service.LoginService;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.Map;

@Slf4j
@Controller
public class LoginController {

    @Autowired
    private LoginService loginService;

    @GetMapping("/")
    public String loginPage() {
        return "index"; // 로그인 페이지 반환
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(String member_id, String password, HttpSession session) {
//        log.info("member_id: " + member_id);
//        log.info("password: " + password);
        User user = loginService.login(member_id, password);
        if (user != null) {
            log.info("로그인객체: {}", user);
            session.setAttribute("user", user);
            return ResponseEntity.ok().body(Map.of("status", "success"));
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("status", "error", "message", "아이디 또는 비밀번호를 확인해주세요"));
        }
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate(); // 세션 삭제
        return "redirect:/";
    }
}
