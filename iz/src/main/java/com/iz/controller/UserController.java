package com.iz.controller;

import com.iz.entity.User;
import com.iz.service.UserService;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Slf4j
@Controller
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("/register")
    public String register() {
        return "register";
    }

    @PostMapping("/register")
    public String register(User user, HttpSession session, Model model) {
        timeSubstring(user);

        userService.register(user);
        session.setAttribute("user", user);
        return "main";
    }

    private static void timeSubstring(User user) {
        String startTime = user.getStart_time();
        String endTime = user.getEnd_time();
        String lunchTime = user.getLunch_time();
        startTime = startTime.substring(0, 5) + ":00";
        endTime = endTime.substring(0, 5) + ":00";
        lunchTime = lunchTime.substring(0, 5) + ":00";
        user.setStart_time(startTime);
        user.setEnd_time(endTime);
        user.setLunch_time(lunchTime);
    }
}
