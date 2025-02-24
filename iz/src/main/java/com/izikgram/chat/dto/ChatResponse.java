package com.izikgram.chat.dto;

import lombok.Data;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Data
public class ChatResponse {
    private String sentiment; // 감정 분석 결과
    private int score; // 감정 점수

    public ChatResponse(String sentiment, int score) {
        this.sentiment = sentiment;
        this.score = score;
    }

    // API 응답의 content로부터 감정 분석 결과와 점수를 추출하는 메서드 추가
    public static ChatResponse fromApiResponse(String content) {
        // 감정 분석 결과와 점수를 추출
        String sentiment = extractSentiment(content);
        int score = extractScore(content);
        return new ChatResponse(sentiment, score);
    }

    // 감정 분석 결과 추출 (예: "분노 감정이 감지되지 않습니다.")
    private static String extractSentiment(String text) {
        // 예시: "분노 감정이 감지되지 않습니다." -> "분노 감정"
        Pattern pattern = Pattern.compile("([가-힣]+ 감정)");
        Matcher matcher = pattern.matcher(text);
        if (matcher.find()) {
            return matcher.group(1);
        }
        return "알 수 없음";  // 감정 분석 결과가 없을 경우
    }

    // 점수 추출 (예: "점수는 0점입니다."에서 숫자만 추출)
    private static int extractScore(String text) {
        Pattern pattern = Pattern.compile("(\\d+)점");
        Matcher matcher = pattern.matcher(text);
        if (matcher.find()) {
            return Integer.parseInt(matcher.group(1));
        }
        return 0;
    }
}