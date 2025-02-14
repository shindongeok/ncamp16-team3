package com.izikgram.user.controller;

import com.izikgram.user.entity.User;
import com.izikgram.user.service.LoginService;
import com.izikgram.user.service.UserService;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Map;

@Slf4j
@Controller
public class LoginController {

    @Autowired
    private LoginService loginService;


    @GetMapping("/")
    public String login() {
        return "index";
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
        session.invalidate();
        return "redirect:/";
    }
}
