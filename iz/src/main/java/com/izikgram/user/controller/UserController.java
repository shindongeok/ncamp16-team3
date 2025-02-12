package com.izikgram.user.controller;

import com.izikgram.user.entity.User;
import com.izikgram.user.service.UserService;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Slf4j
@Controller
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("/register")
    public String register() {
        return "/user/register";
    }

    @PostMapping("/register")
    public String register(User user) {
        timeSubstring(user);

        userService.register(user);
        return "redirect:/";
    }

    private static void timeSubstring(User user) {
        String startTime = user.getStart_time();
        String endTime = user.getEnd_time();
        String lunchTime = user.getLunch_time();

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

    // 마이페이지
    @GetMapping("/mypage")
    public String mypage(HttpSession session) {
        User user = (User) session.getAttribute("user");
        log.info("User: {}", user);
        return "/user/mypage";
    }

    //개인 정보 수정
    @GetMapping("/update")
    public String update(HttpSession session) {
        User user = (User) session.getAttribute("user");
        log.info("User: {}", user);
        return "/user/update";
    }

    //계정 관리
    @GetMapping("/accountManagement")
    public String accountManagement() {
        return "/user/accountManagement";
    }

    //비밀번호 확인(비밀번호 변경 클릭시)
    @GetMapping("/checkPwd")
    public String checkPwd(HttpSession session) {
        User user = (User) session.getAttribute("user");
        System.out.println("세션비밀번호: " + user.getPassword());

        log.info("User: {}", user);
        return "/user/checkPwd";
    }

    @PostMapping("/checkPwd")
    public String verifyPassword(@RequestParam("password") String password, HttpSession session) {

        User user = (User) session.getAttribute("user");
        log.info("세션 유저: {}", user);

        if (user != null) {
            log.info("세션비밀번호: {}", user.getPassword());
            log.info("입력한 비밀번호: {}", password);

            if (user.getPassword().equals(password)) {
                return "redirect:/user/updatePw";
            } else {
                return "redirect:/user/checkPwd";
            }
        }
        return "redirect:/user/checkPwd";
    }


    @PostMapping("/updatePw")
    public String updatePw(@RequestParam("password") String password, HttpSession session) {
        // 세션에서 로그인된 사용자 정보 가져오기
        User user = (User) session.getAttribute("user");

        // 비밀번호 변경 로직 실행 1이면 변경 성공 0이면 실패
        boolean isUpdated = userService.updateUserPw(user.getMember_id(), password);

        if (isUpdated) {
            session.invalidate(); // 비밀번호 변경 후 로그아웃
            return "redirect:/"; // 성공 시 로그인 페이지로 이동
        } else {
            return "redirect:/user/updatePw"; // 실패 시 다시 비밀번호 변경 페이지
        }
    }


    // 비밀번호 변경 페이지 보여주기
    @GetMapping("/updatePw")
    public String updatePw() {
        return "/user/updatePw";
    }

    @PostMapping("/delete")
    public String user_delete(String member_id) {
        log.info("User id: {}", member_id);
        userService.deleteUser(member_id);
        return "redirect:/";
    }

}