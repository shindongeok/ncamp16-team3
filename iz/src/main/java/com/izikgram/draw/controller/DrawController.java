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

        // 입력값 검증
        if (playersInput == null || playersInput.trim().isEmpty() ||
                resultsInput == null || resultsInput.trim().isEmpty()) {
            model.addAttribute("message", "플레이어와 결과를 입력해주세요.");
            return "draw";
        }

        try {
            // 플레이어 목록 처리
            List<String> players = Arrays.stream(playersInput.split(","))
                    .map(String::trim)
                    .filter(s -> !s.isEmpty())
                    .collect(Collectors.toList());

            if (players.isEmpty()) {
                model.addAttribute("message", "유효한 플레이어 이름을 입력해주세요.");
                return "draw";
            }

            // 결과를 정수 리스트로 변환 시도
            List<Integer> results;
            try {
                results = Arrays.stream(resultsInput.split(","))
                        .map(String::trim)
                        .filter(s -> !s.isEmpty())
                        .map(Integer::parseInt)
                        .collect(Collectors.toList());

                if (results.isEmpty() || results.stream().anyMatch(r -> r <= 0)) {
                    model.addAttribute("message", "상품 개수는 0보다 큰 숫자여야 합니다.");
                    return "draw";
                }
            } catch (NumberFormatException e) {
                model.addAttribute("message", "상품 개수는 숫자로만 입력해주세요.");
                return "draw";
            }

            // 상품 총 개수 확인
            int totalPrizes = results.stream().mapToInt(Integer::intValue).sum();
            if (totalPrizes > players.size()) {
                model.addAttribute("message", "상품 개수(" + totalPrizes +
                        "개)가 플레이어 수(" + players.size() + "명)보다 많습니다.\n상품 개수를 줄이거나 플레이어 수를 늘려주세요.");
                // 에러 메시지만 표시하고 결과 페이지로 넘어가지 않음
                return "draw";
            }

            // DrawGame 객체 생성 후 실행
            DrawGame drawGame = new DrawGame(players, results);
            Map<String, String> gameResult = drawGame.play();

            // 결과를 모델에 담아 전달
            model.addAttribute("gameResult", gameResult);

        } catch (Exception e) {
            // 예외 발생 시 로그 출력 및 오류 메시지 전달
            System.err.println("게임 실행 중 오류 발생: " + e.getMessage());
            e.printStackTrace();
            model.addAttribute("message", "게임 실행 중 오류가 발생했습니다: " + e.getMessage());
            return "draw";
        }

        return "draw";
    }
}