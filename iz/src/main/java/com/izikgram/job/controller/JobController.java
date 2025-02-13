package com.izikgram.job.controller;

import com.izikgram.job.service.JobService;
import com.izikgram.user.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/job")
public class JobController {


    @Autowired
    private JobService jobService;

    @GetMapping("/")
    public String hire(String member_id, Model model){

        User user=jobService.getUserById(member_id);
        model.addAttribute("user", user);

        return "job/hire";

    }

    @GetMapping("/scrap")
    public String scrap(String member_id, Model model){

        User user=jobService.getUserById(member_id);
        model.addAttribute("user", user);

        return "job/scrap";
    }
}
