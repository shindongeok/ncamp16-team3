package com.izikgram.user.controller;

import com.izikgram.user.entity.User;
import com.izikgram.user.service.LoginService;
import com.izikgram.user.service.UserService;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Slf4j
@Controller
public class LoginController {

    @Autowired
    private LoginService loginService;

    @Autowired
    private UserService userService;

    @GetMapping("/")
    public String login() {
        return "index";
    }

    @PostMapping("/login")
    public String login(String member_id, String password, HttpSession session) {
//        log.info("member_id: " + member_id);
//        log.info("password: " + password);
        User user = loginService.login(member_id, password);
        session.setAttribute("user", user);
        log.info("로그인객체: {}", user);
        return "redirect:/main";
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/";
    }
}
