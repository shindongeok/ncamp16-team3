package com.izikgram.job.controller;

import com.izikgram.global.security.CustomUserDetails;
import com.izikgram.job.entity.Job;
import com.izikgram.job.service.JobService;
import com.izikgram.user.entity.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Slf4j
@Controller
@RequestMapping("/job")
@RequiredArgsConstructor
public class JobController {

    private final JobService jobService;

    @GetMapping("/hire")
    public String hire(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestParam(required = false) String loc_mcd,
            @RequestParam(required = false) String ind_cd,
            @RequestParam(required = false) String edu_lv,
            @RequestParam(defaultValue = "0") int offset,
            Model model) {

        User user = userDetails.getUser();

        String finalLoc = loc_mcd != null ? loc_mcd : user.getLoc_mod();
        String finalInd = ind_cd != null ? ind_cd : user.getInd_cd();
        String finalEdu = edu_lv != null ? edu_lv : user.getEdu_lv();

        try {
            String encodedLoc = URLEncoder.encode(finalLoc, StandardCharsets.UTF_8);
            System.out.println("Encoded Location: " + encodedLoc);

            List<Job> allJobs = jobService.searchJobs(finalLoc, finalInd, finalEdu);

            List<Job> deadlineJobs = jobService.getDeadlineJobs(allJobs, offset);
            List<Job> recentJobs = jobService.getRecentJobs(allJobs, offset);

            model.addAttribute("deadlineJobs", deadlineJobs);
            model.addAttribute("recentJobs", recentJobs);

        } catch (Exception e) {
            log.error("Error fetching jobs", e);
        }

        try {
            List<Job> allJobs = jobService.searchJobs(finalLoc, finalInd, finalEdu);

            // offset을 고려하여 데이터 처리
            List<Job> deadlineJobs = jobService.getDeadlineJobs(allJobs, offset);
            List<Job> recentJobs = jobService.getRecentJobs(allJobs, offset);

            model.addAttribute("deadlineJobs", deadlineJobs);
            model.addAttribute("recentJobs", recentJobs);
            model.addAttribute("hasMore", deadlineJobs.size() == 5 || recentJobs.size() == 5);  // 더 보여줄 데이터가 있는지 확인

        } catch (Exception e) {
            log.error("Error fetching jobs", e);
        }

        return "job/hire";
    }
}