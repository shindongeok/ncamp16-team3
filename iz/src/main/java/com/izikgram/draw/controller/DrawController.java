package com.izikgram.draw.controller;

import com.izikgram.draw.service.DrawGame;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/draw")
public class DrawController {

    @GetMapping("/draw")
    public String startLadderGame(@RequestParam(value = "players", required = false) String playersInput,
                                  @RequestParam(value = "results", required = false) String resultsInput,
                                  Model model) {

        if (playersInput == null || resultsInput == null) {
            // 예시: 입력이 없으면 메시지나 기본값 처리
            model.addAttribute("message", "플레이어와 결과를 입력해주세요.");
            return "draw"; // 같은 페이지로 리턴
        }

        // 플레이어 목록
        List<String> players = Arrays.asList(playersInput.split(","));

        // 결과를 정수 리스트로 변환
        List<Integer> results = Arrays.stream(resultsInput.split(","))
                .map(String::trim)  // 공백 제거
                .map(Integer::parseInt)
                .collect(Collectors.toList());

        // LadderGame 객체 생성 후 실행
        DrawGame ladderGame = new DrawGame(players, results);
        Map<String, String> gameResult = ladderGame.play();

        // 결과를 모델에 담아 전달
        model.addAttribute("gameResult", gameResult);
        return "draw"; // 같은 페이지로 리턴
    }
}
