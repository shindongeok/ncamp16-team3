package com.izikgram.user.controller;

import com.izikgram.user.entity.User;
import com.izikgram.user.service.UserService;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.HashMap;
import java.util.Map;

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

    @PostMapping("/findId")
    public String findId(String name, Model model) {
        String id = userService.findId(name);
        model.addAttribute("member_id", id);
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
//    @GetMapping("/mypage")
//    public String mypage(HttpSession session) {
//        User user = (User) session.getAttribute("user");
//        log.info("User: {}", user);
//        return "/user/mypage";
//    }
    @GetMapping("/mypage")
    public String mypage(Authentication authentication, Model model) {
        // Authentication 객체에서 사용자 정보 가져오기
        String member_id = authentication.getName(); // 로그인한 사용자 이름
        User user = userService.getUserInfo(member_id); // 사용자 정보 조회
        log.info("User = {}", user);

        // 모델에 사용자 정보를 담아 뷰로 전달
        model.addAttribute("user", user);

        return "/user/mypage";
    }


    //계정 관리
    @GetMapping("/accountManagement")
    public String accountManagement(HttpSession session) {
        User user = (User) session.getAttribute("user");
        log.info("User: {}", user);
        return "/user/accountManagement";
    }

    //개인 정보 수정
//    @GetMapping("/update")
//    public String update(HttpSession session, Model model) {
//        User user = (User) session.getAttribute("user");
//        model.addAttribute("user", user);
//        return "/user/update";
//    }

    @GetMapping("/update")
    public String update(Authentication authentication, Model model) {
        String member_id = authentication.getName();
        User user = userService.getUserInfo(member_id);
        log.info("User = {}", user);

        model.addAttribute("user", user);
        return "/user/update";
    }

    @PostMapping("/update")
    public String updateUser(User user, HttpSession session) {
        User updatefield = (User) session.getAttribute("user");

        // 폼에서 전송된 데이터만 업데이트
        updatefield.setNickname(user.getNickname());
        updatefield.setEmail(user.getEmail());
        updatefield.setIntro(user.getIntro());
        updatefield.setBirth_date(user.getBirth_date());
        updatefield.setLoc_mod(user.getLoc_mod());
        updatefield.setInd_cd(user.getInd_cd());
        updatefield.setEdu_lv(user.getEdu_lv());
        updatefield.setPayday(user.getPayday());

        userService.updateUser(updatefield);
        log.info("변경된 User 객체 = {}", updatefield);
        session.setAttribute("user", updatefield);

        return "redirect:/main";
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
    public String user_delete(HttpSession session) {
        User user = (User) session.getAttribute("user");
        log.info("User id: {}", user.getMember_id());
        userService.deleteUser(user.getMember_id());
        return "redirect:/";
    }

}