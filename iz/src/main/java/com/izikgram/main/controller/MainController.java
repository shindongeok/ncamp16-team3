package com.izikgram.main.controller;

import com.izikgram.board.entity.Board;
import com.izikgram.main.service.MainService;
import com.izikgram.user.entity.User;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/main")
@RequiredArgsConstructor
public class MainController {

    private final MainService mainService;

    @GetMapping
    public String main(Model model, HttpSession session) {

        // 인기글 3개 가져오기
        List<Board> popularBoardList = mainService.getPopularBoardList();
        model.addAttribute("popularBoardList", popularBoardList);

        // 캘린더 feeling 가져오기
        User user = (User) session.getAttribute("user");
        LocalDate now = LocalDate.now();
        List<Map<String, Object>> feelingList = mainService.getMonthlyFeeling(user.getMember_id(), now.format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM")));
        model.addAttribute("feelingList", feelingList);

        return "/main/main";
    }
}
