package com.izikgram.user.controller;

import com.izikgram.global.security.CustomUserDetails;
import com.izikgram.user.entity.User;
import com.izikgram.user.service.UserService;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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

//    @PostMapping("/register")
//    public String register(User user) {
////        log.info("payday: {}", user.getPayday());
////        log.info("user: {}", user);
//        timeSubstring(user);
//
//        userService.register(user);
//        return "redirect:/";
//    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody User user) {
        log.info("user: {}", user);

        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("실패");
        }

        String password = user.getPassword();
        String encodePw = passwordEncoder.encode(password);
        user.setPassword(encodePw);

        timeSubstring(user);

        userService.register(user);

        return ResponseEntity.ok("비밀번호 변경 성공");
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
        // 모두 AuthController에서 처리
//        String phoneNum = (String) session.getAttribute("phone_num");
//        String name = (String) session.getAttribute("name");
////        log.info("phone_num: {}, name: {}", phoneNum, name);
//        User user = userService.findUserByPhoneNumber(name, phoneNum);
//        String member_id = user.getMember_id();
//        session.setAttribute("member_id", member_id);
////        log.info("findIdResult: phoneNum={}, member_id={}", phoneNum, member_id);
        return "/user/findIdResult";
    }

    @GetMapping("/findPw")
    public String findPw() {
        return "/user/findPw";
    }

//    @PostMapping("/findPw")
//    public String findPw(String member_id, Model model) {
//        boolean object = userService.findPassword(member_id);
////        log.info("member_id: " + member_id + ", object: " + object);
//        if (object) {
//            model.addAttribute("member_id", member_id);
////            log.info("member_id: " + member_id);
//            return "/user/findPwResult";
//        } else {
//            return "redirect:/user/findPw";
//        }
//    }

    @GetMapping("/findPwResult")
    public String findPwResult() {
        return "/user/findPwResult";
    }

//    @PostMapping("/resetPw")
//    public String resetPw(String password, HttpSession session) {;
//        String member_id = (String) session.getAttribute("member_id");
//        log.info("member_id: {}", member_id);
//        log.info("password: {}", password);
//        String encodePw = passwordEncoder.encode(password);
//        userService.updatePassword(member_id, encodePw);
//        return "redirect:/";
//    }

    @PostMapping("/resetPw")
    public ResponseEntity<?> resetPw(@RequestParam String password, HttpSession session) {
        String member_id = (String) session.getAttribute("member_id");
//        log.info("member_id: {}", member_id);
//        log.info("password: {}", password);

        if (member_id == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("세션 만료. 다시 로그인하세요.");
        }

        String encodePw = passwordEncoder.encode(password);
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
        User deleteUser = userDetails.getUser();
        userService.deleteUser(deleteUser.getMember_id()); // DB에서 status 변경
        return "redirect:/";
    }
}