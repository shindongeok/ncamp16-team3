package com.izikgram.job.controller;

import com.izikgram.alarm.service.AlarmService;
import com.izikgram.global.security.CustomUserDetails;
import com.izikgram.job.entity.Job;
import com.izikgram.job.entity.JobDto;
import com.izikgram.job.service.JobService;
import com.izikgram.user.entity.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Controller
@RequestMapping("/job")
@RequiredArgsConstructor
public class JobController {

    @Autowired
    private final JobService jobService;

    @Autowired
    private AlarmService alarmService;

    @GetMapping("/hire")
    public String hire(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestParam(required = false) String loc_mcd,
            @RequestParam(required = false) String ind_cd,
            @RequestParam(required = false) String edu_lv,
            @RequestParam(defaultValue = "0") int offset,
            Model model) {

        User user = userDetails.getUser();
        String memberId = user.getMember_id();

        String finalLoc = loc_mcd != null ? loc_mcd : user.getLoc_mod();
        String finalInd = ind_cd != null ? ind_cd : user.getInd_cd();
        String finalEdu = edu_lv != null ? edu_lv : user.getEdu_lv();

        try {
            List<Job> allJobs = jobService.searchJobs(finalLoc, finalInd, finalEdu);

            List<Job> deadlineJobs = jobService.getDeadlineJobs(allJobs, offset);
            List<Job> recentJobs = jobService.getRecentJobs(allJobs, offset);

            // 각 job에 대해 스크랩 여부 확인
            for (Job job : deadlineJobs) {
                boolean isScraped = jobService.checkIfScraped(job.getId(), memberId);
                model.addAttribute("isScraped_" + job.getId(), isScraped);
            }

            for (Job job : recentJobs) {
                boolean isScraped = jobService.checkIfScraped(job.getId(), memberId);
                model.addAttribute("isScraped_" + job.getId(), isScraped);
            }

            model.addAttribute("deadlineJobs", deadlineJobs);
            model.addAttribute("recentJobs", recentJobs);
            model.addAttribute("hasMore", deadlineJobs.size() == 5 || recentJobs.size() == 5);

        } catch (Exception e) {
            log.error("Error fetching jobs", e);
        }

        return "job/hire";
    }

    @PostMapping("/scrap/{jobId}")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> toggleScrap(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable String jobId,
            @RequestBody JobDto jobDto) {
        log.info("Received JobDto: {}", jobDto);  // JobDto 로깅
        try {
            String unixTimeStamp = String.valueOf(jobDto.getExpirationTimestamp());  // 'long' 값을 'String'으로 변환

            if (unixTimeStamp == null || unixTimeStamp.isEmpty()) {
                throw new IllegalArgumentException("Expiration timestamp is invalid");
            }

            long timestamp = Long.parseLong(unixTimeStamp);

            // Unix Timestamp를 DATETIME 형식으로 변환
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date date = new Date();
            date.setTime(timestamp * 1000);  // 밀리초로 변환

            // 변환된 DATETIME 문자열ㅁ
            String datetime = sdf.format(date);
            jobDto.setExpirationTimestamp(datetime);

            String memberId = userDetails.getUser().getMember_id();
            jobDto.setId(jobId);

            boolean isScraped = jobService.toggleScrap(jobDto, memberId);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("isScraped", isScraped);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error occurred: {}", e.getMessage());
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Failed to toggle scrap");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @GetMapping("/scrap/check/{jobId}")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> checkScrap(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable String jobId) {
        try {
            String memberId = userDetails.getUser().getMember_id();
            boolean isScraped = jobService.checkIfScraped(jobId, memberId);

            Map<String, Object> response = new HashMap<>();
            response.put("isScraped", isScraped);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @GetMapping("/scrap")
    public String scrap(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            Model model) {
        User user = userDetails.getUser();
        String memberId = user.getMember_id();

        try {
            List<Job> scrapedJobs = jobService.getScrapedJobs(memberId);
            model.addAttribute("scrapedJobs", scrapedJobs);
        } catch (Exception e) {
            log.error("Error fetching scraped jobs", e);
        }

        return "job/scrap";
    }
}