package com.iz.controller;

import com.iz.service.LoginService;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

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
    public String login(@RequestBody String member_id, String password, HttpSession session) {
        // member_id 로그: member_id: member_id=hello&password=1234
        // password 로그: password: 1234
//        log.debug("member_id: " + member_id);
//        log.debug("password: " + password);
        loginService.login(member_id, password);
        session.setAttribute("member_id", member_id);
        return "redirect:/main";
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/";
    }
}
