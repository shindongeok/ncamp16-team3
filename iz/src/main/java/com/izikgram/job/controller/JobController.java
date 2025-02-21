package com.izikgram.job.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.izikgram.global.security.CustomUserDetails;
import com.izikgram.job.service.JobService;
import com.izikgram.user.entity.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Controller
@RequestMapping("/job")
@RequiredArgsConstructor
public class JobController {

    @Autowired
    private JobService jobService;

    @GetMapping("/hire")
    public String hire(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestParam(required = false) String loc_mcd,
            @RequestParam(required = false) String ind_cd,
            @RequestParam(required = false) String edu_lv,
            Model model) {

        log.info("User 객체 : {}", userDetails.getUser());
        User user = userDetails.getUser();

        String finalLoc = loc_mcd != null ? loc_mcd : user.getLoc_mod();
        String finalInd = ind_cd != null ? ind_cd : user.getInd_cd();
        String finalEdu = edu_lv != null ? edu_lv : user.getEdu_lv();

        String apiResponse = jobService.searchJobs(finalLoc, finalInd, finalEdu);

        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode = objectMapper.readTree(apiResponse);
            log.info("API Response Structure: {}", rootNode.toPrettyString());  // 전체 응답 구조 확인
            JsonNode jobsNode = rootNode.path("jobs").path("job");
            List<JsonNode> jobsList = new ArrayList<>();
            jobsNode.forEach(jobsList::add);

            // 마감일 기준으로 정렬
            List<JsonNode> deadlineJobs = jobsList.stream()
                    .sorted((a, b) -> {
                        String dateA = a.path("expiration-date").asText();
                        String dateB = b.path("expiration-date").asText();
                        return dateA.compareTo(dateB);
                    })
                    .limit(5)
                    .collect(Collectors.toList());

            // 등록일 기준으로 정렬
            List<JsonNode> recentJobs = jobsList.stream()
                    .sorted((a, b) -> {
                        String dateA = a.path("posting-timestamp").asText();
                        String dateB = b.path("posting-timestamp").asText();
                        return dateB.compareTo(dateA);
                    })
                    .limit(5)
                    .collect(Collectors.toList());

            model.addAttribute("deadlineJobs", deadlineJobs);
            model.addAttribute("recentJobs", recentJobs);

        } catch (Exception e) {
            log.error("Error parsing API response", e);
        }

        return "job/hire";
    }

    @GetMapping("/")
    public String redirectToHire() {
        return "redirect:/job/hire";
    }
}