package com.izikgram.user.controller;

import com.izikgram.global.security.CustomUserDetails;
import com.izikgram.job.entity.Job;
import com.izikgram.job.service.JobService;
import com.izikgram.user.dto.PasswordDTO;
import com.izikgram.user.entity.User;
import com.izikgram.user.service.UserService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
@Controller
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @GetMapping("/register")
    public String register(Model model) {
        model.addAttribute("user", new User());
        return "user/register";
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody User user, BindingResult bindingResult) {
//        log.info("user: {}", user);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("실패");
        }
        if (bindingResult.hasErrors()) {
            Map<String, String> errors = bindingResult.getFieldErrors().stream()
                    .collect(Collectors.toMap(
                            error -> error.getField(),
                            error -> error.getDefaultMessage()
                    ));

            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors);
        }
        timeSubstring(user);
        userService.register(user);
        return ResponseEntity.ok("회원가입 성공");
    }

    @ResponseBody
    @GetMapping("/checkId/{member_id}")
    public ResponseEntity<?> checkUsername(@PathVariable String member_id) {
//        log.info("check id: {}", member_id);
        if (!member_id.matches("[a-zA-Z0-9]{6,20}")) {
//            log.info("매치 오류");
            return ResponseEntity.badRequest().body(Map.of("error", "아이디는 6~20자 영문과 숫자만 가능합니다."));
        }
        boolean isExist = userService.userIdCheck(member_id);
        return ResponseEntity.ok(Map.of("exist", isExist));
    }

    @GetMapping("/findId")
    public String findId() {
        return "user/findId";
    }

    @GetMapping("/findIdResult")
    public String findIdResult(HttpSession session) {
        return "user/findIdResult";
    }

    @GetMapping("/findPw")
    public String findPw() {
        return "user/findPw";
    }

    @GetMapping("/findPwResult")
    public String findPwResult(Model model) {
        model.addAttribute("user", new User());
        return "user/findPwResult";
    }

    @PostMapping("/resetPw")
    public ResponseEntity<?> resetPw(@Valid @RequestBody PasswordDTO passwordDTO, BindingResult bindingResult, HttpSession session) {
        String member_id = (String) session.getAttribute("member_id");

        if (member_id == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("세션 만료. 다시 로그인하세요.");
        }
        if (bindingResult.hasErrors()) {
            Map<String, String> errors = bindingResult.getFieldErrors().stream()
                    .collect(Collectors.toMap(
                            error -> error.getField(),
                            error -> error.getDefaultMessage()
                    ));

            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors);
        }

        String encodePw = passwordEncoder.encode(passwordDTO.getPassword());
        userService.updatePassword(member_id, encodePw);

        return ResponseEntity.ok("비밀번호 변경 성공");
    }

    private static void timeSubstring(User user) {
        String startTime = user.getStart_time();
        String endTime = user.getEnd_time();
        String lunchTime = user.getLunch_time();
//        log.info("startTime : {}, endTime : {}, lunchTime : {}", startTime, endTime, lunchTime);

        startTime = AmPm(startTime);
        endTime = AmPm(endTime);
        lunchTime = AmPm(lunchTime);

        user.setStart_time(startTime);
        user.setEnd_time(endTime);
        user.setLunch_time(lunchTime);
    }

    private static String AmPm(String time) {
        String meridian = time.substring(6);
//        log.debug("meridian: " + meridian);

        if (meridian.equals("PM")) {
            String result;
            int hour = Integer.parseInt(time.substring(0, 2));
            if (hour == 12) {
                time = time.substring(0, 5) + ":00";
                return time;
            }
//            log.debug("hour: " + hour);
            String stringHour = String.valueOf(hour + 12);
//            log.debug("stringHour: " + stringHour);

            String substring = time.substring(2);
            result = stringHour.concat(substring);
//            log.debug("result: " + result);

            time = result.substring(0, 5) + ":00";
//            log.debug("startTime: " + time);
        } else {
            time = time.substring(0, 5) + ":00";
//            log.debug("startTime: " + time);
        }
        return time;
    }

    @GetMapping("/mypage")
    public String mypage() {
        return "user/mypage";
    }

    //계정 관리
    @GetMapping("/accountManagement")
    public String accountManagement() {
        return "user/accountManagement";
    }


    @GetMapping("/update")
    public String update() {
        return "user/update";
    }

    @PostMapping("/update")
    public String updateUser(@AuthenticationPrincipal CustomUserDetails userDetails, User user) {
        User loginUser = userDetails.getUser();
        loginUser.setNickname(user.getNickname());
        loginUser.setEmail(user.getEmail());
        loginUser.setIntro(user.getIntro());
        loginUser.setBirth_date(user.getBirth_date());
        loginUser.setLoc_mod(user.getLoc_mod());
        loginUser.setInd_cd(user.getInd_cd());
        loginUser.setEdu_lv(user.getEdu_lv());
        loginUser.setPayday(user.getPayday());
        userService.updateUser(loginUser);

        log.info("변경된 사용자 정보 : {}", loginUser);
        return "redirect:/main";
    }


    //비밀번호 확인(비밀번호 변경 클릭시)
    @GetMapping("/checkPwd")
    public String checkPwd() {
        return "user/checkPwd";
    }

    @PostMapping("/checkPwd")
    public String verifyPassword(@AuthenticationPrincipal CustomUserDetails userDetails, User user,
                                 @RequestParam("password") String password) {
        if(passwordEncoder.matches(password, userDetails.getUser().getPassword())) {
            log.info("사용자 현재 비밀번호: {}", userDetails.getUser().getPassword());
            log.info("입력한 비밀번호: {}", password);
            return "redirect:/user/updatePw";
        } else {
            return "redirect:/user/checkPwd";
        }
    }

    // 비밀번호 변경 페이지 보여주기
    @GetMapping("/updatePw")
    public String updatePw() {
        return "user/updatePw";
    }

    @PostMapping("/updatePw")
    public String updatePw(@AuthenticationPrincipal CustomUserDetails userDetails, User user,
                           @RequestParam("password") String newPassword) {
        String encodedPassword = passwordEncoder.encode(newPassword);
        User loginUser = userDetails.getUser();
        loginUser.setPassword(encodedPassword);
        userService.updateUserPw(loginUser.getPassword(), loginUser.getMember_id());
        return "redirect:/";
    }


    @PostMapping("/delete")
    public String user_delete(@AuthenticationPrincipal CustomUserDetails userDetails) {
        User deleteUser = userDetails.getUser();
        userService.deleteUser(deleteUser.getMember_id()); // DB에서 status 변경
        return "redirect:/";
    }

    @GetMapping("/analyze")
    public String analyzePage() {
        return "user/chat";
    }

    private final JobService jobService;

    @PostMapping("/analyze/recommend-jobs")
    @ResponseBody
    public List<Job> getRecommendedJobs(
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        User user = userDetails.getUser();

        // 사용자의 기본 검색 조건 사용
        String location = user.getLoc_mod();
        String industry = user.getInd_cd();
        String educationLevel = user.getEdu_lv();

        try {
            // JobService의 기존 searchJobs 메서드 활용
            List<Job> allJobs = jobService.searchJobs(location, industry, educationLevel);

            // 최근 공고와 마감 임박 공고를 조합하여 최대 2개 추천
            List<Job> recentJobs = jobService.getRecentJobs(allJobs, 0);
            List<Job> deadlineJobs = jobService.getDeadlineJobs(allJobs, 0);

            // 중복 제거 및 최대 2개 선택
            return Stream.concat(recentJobs.stream(), deadlineJobs.stream())
                    .distinct()
                    .limit(2)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            log.error("Error fetching recommended jobs", e);
            return new ArrayList<>(); // 오류 발생 시 빈 리스트 반환
        }
    }

    @PostMapping("/updateTime")
    @ResponseBody
    public ResponseEntity<?> updateTime(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestParam("startTime") String startTime,
            @RequestParam("lunchTime") String lunchTime,
            @RequestParam("endTime") String endTime) {

        try {
            log.info("Received times - startTime: {}, lunchTime: {}, endTime: {}",
                    startTime, lunchTime, endTime);

            String memberId = userDetails.getUser().getMember_id();

            // 시간 변환 메서드 수정
            startTime = convertTo24HourFormat(startTime);
            lunchTime = convertTo24HourFormat(lunchTime);
            endTime = convertTo24HourFormat(endTime);

            log.info("Converted times - startTime: {}, lunchTime: {}, endTime: {}",
                    startTime, lunchTime, endTime);

            userService.updateUserTime(memberId, startTime, lunchTime, endTime);

            return ResponseEntity.ok(Map.of(
                    "startTime", startTime,
                    "lunchTime", lunchTime,
                    "endTime", endTime
            ));
        } catch (Exception e) {
            log.error("시간 업데이트 중 오류 발생", e);
            return ResponseEntity.badRequest().body("시간 업데이트 실패: " + e.getMessage());
        }
    }

    // 24시간 형식으로 변환하는 새로운 메서드
    private String convertTo24HourFormat(String time) {
        // 이미 24시간 형식이면 그대로 반환
        if (time.matches("\\d{2}:\\d{2}")) {
            return time;
        }

        throw new IllegalArgumentException("잘못된 시간 형식: " + time);
    }
}