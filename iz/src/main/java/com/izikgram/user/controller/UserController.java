package com.izikgram.user.controller;

import com.izikgram.global.security.CustomUserDetails;
import com.izikgram.user.entity.User;
import com.izikgram.user.service.UserService;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Controller
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @GetMapping("/register")
    public String register() {
        return "/user/register";
    }

    @PostMapping("/register")
    public String register(User user) {
//        log.info("payday: {}", user.getPayday());
//        log.info("user: {}", user);
        timeSubstring(user);

        userService.register(user);
        return "redirect:/";
    }

    @ResponseBody
    @GetMapping("/checkId/{member_id}")
    public Map<String, Boolean> checkUsername(@PathVariable String member_id) {
        Map<String, Boolean> result = new HashMap<>();
        boolean isExist = userService.userIdCheck(member_id);
//        log.info("check id {}", member_id);
//        log.info("isExist {}", isExist);
        result.put("exist", isExist);
        return result;
    }

    @GetMapping("/findId")
    public String findId() {
        return "/user/findId";
    }

//    @PostMapping("/findId")
//    public String findId(String name, Model model) {
//        String id = userService.findId(name);
//        model.addAttribute("member_id", id);
//        return "/user/findIdResult";
//    }

    @GetMapping("/findIdResult")
    public String findIdResult(HttpSession session) {
        String phoneNum = (String) session.getAttribute("phone_num");
        String member_id = userService.findUserByPhoneNumber(phoneNum);
        session.setAttribute("member_id", member_id);
//        log.info("findIdResult: phoneNum={}, member_id={}", phoneNum, member_id);
        return "/user/findIdResult";
    }

    @GetMapping("/findPw")
    public String findPw() {
        return "/user/findPw";
    }

    @PostMapping("/findPw")
    public String findPw(String member_id, Model model) {
        boolean object = userService.findPassword(member_id);
//        log.info("member_id: " + member_id + ", object: " + object);
        if (object) {
            model.addAttribute("member_id", member_id);
//            log.info("member_id: " + member_id);
            return "/user/findPwResult";
        } else {
            return "redirect:/user/findPw";
        }
    }

    @PostMapping("/resetPw")
    public String resetPw(String member_id, String password) {
//        log.info("member_id: " + member_id + ", password: " + password);
        userService.updatePassword(member_id, password);
        return "redirect:/";
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
        return "/user/mypage";
    }

    //계정 관리
    @GetMapping("/accountManagement")
    public String accountManagement() {
        return "/user/accountManagement";
    }


    @GetMapping("/update")
    public String update() {
        return "/user/update";
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
        return "/user/checkPwd";
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
        return "/user/updatePw";
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
        User loginUser = userDetails.getUser();
        userService.deleteUser(loginUser.getMember_id());
        return "redirect:/";
    }
}