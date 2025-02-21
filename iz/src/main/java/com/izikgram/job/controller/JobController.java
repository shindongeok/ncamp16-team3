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
            Model model) {

        User user = userDetails.getUser();

        String finalLoc = loc_mcd != null ? loc_mcd : user.getLoc_mod();
        String finalInd = ind_cd != null ? ind_cd : user.getInd_cd();
        String finalEdu = edu_lv != null ? edu_lv : user.getEdu_lv();

        System.out.println("Final loc: " + finalLoc);
        System.out.println("Final ind: " + finalInd);
        System.out.println("Final edu: " + finalEdu);

        try {
            String encodedLoc = URLEncoder.encode(finalLoc, StandardCharsets.UTF_8);
            System.out.println("Encoded Location: " + encodedLoc);

            List<Job> allJobs = jobService.searchJobs(finalLoc, finalInd, finalEdu);

            List<Job> deadlineJobs = jobService.getDeadlineJobs(allJobs);
            List<Job> recentJobs = jobService.getRecentJobs(allJobs);

            // 마감 임박 일자리 정보 출력
            System.out.println("Deadline jobs count: " + deadlineJobs.size());
            for (Job job : deadlineJobs) {
                System.out.println("Deadline Job - Company: " + job.getCompanyName() +
                        ", Title: " + job.getTitle() +
                        ", Location: " + job.getLocationName() +
                        ", Expiration: " + job.getExpirationTimestamp());
            }

            model.addAttribute("deadlineJobs", deadlineJobs);
            model.addAttribute("recentJobs", recentJobs);

        } catch (Exception e) {
            log.error("Error fetching jobs", e);
            // 에러 처리 로직 추가
        }

        return "job/hire";
    }
}