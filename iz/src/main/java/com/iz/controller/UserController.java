package com.iz.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class UserController {

    @GetMapping("/update")
    public String update() {
        return "update";
    }

    @GetMapping("/checkPwd")
    public String checkPwd() {
        return "checkPwd";
    }
}
