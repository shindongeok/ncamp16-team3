package com.izikgram.job.controller;

import com.izikgram.global.security.CustomUserDetails;
import com.izikgram.job.service.JobService;
import com.izikgram.user.entity.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Slf4j
@Controller
@RequestMapping("/job")
public class JobController {


    @Autowired
    private JobService jobService;

//    @GetMapping("/")
//    public String hire(String member_id, Model model){
//
//        User user=jobService.getUserById(member_id);
//        model.addAttribute("user", user);
//
//        return "job/hire";
//
//    }
//
//    @GetMapping("/scrap")
//    public String scrap(String member_id, Model model){
//
//        User user=jobService.getUserById(member_id);
//        model.addAttribute("user", user);
//
//        return "job/scrap";
//    }


    @GetMapping("/")
    public String hire(@AuthenticationPrincipal CustomUserDetails userDetails) {
        // 인증된 사용자 정보를 가져옵니다.
        log.info("User 객체 : {}", userDetails.getUser());

        return "job/hire";
    }

    @GetMapping("/scrap")
    public String scrap(@AuthenticationPrincipal CustomUserDetails userDetails) {
        // 인증된 사용자 정보를 가져옵니다.
        log.info("User 객체 : {}", userDetails.getUser());

        return "job/scrap";
    }
}



