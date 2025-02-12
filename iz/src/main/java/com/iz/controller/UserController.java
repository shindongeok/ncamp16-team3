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

    //마이 페이지
    @GetMapping("/mypage")
    public String mypage(@RequestParam(value = "member_id", required = false) String member_id, Model model) {
        User userInfo = userService.getUserInfo(member_id);

        // User 객체의 내용을 로깅하여 확인
        log.info("User info: {}", userInfo);

        model.addAttribute("user", userInfo);
        return "mypage";
    }



    //개인 정보 수정
    @GetMapping("/update")
    public String update() {

        return "update";
    }

    //계정 관리
    @GetMapping("/accountManagement")
    public String accountManagement() {
        return "accountManagement";
    }

    //비밀번호 확인(비밀번호 변경 클릭시)
    @GetMapping("/checkPwd")
    public String checkPwd() {

        return "checkPwd";
    }

    @GetMapping("/updatePw")
    public String updatePw() {

        return "updatePw";
    }
}
