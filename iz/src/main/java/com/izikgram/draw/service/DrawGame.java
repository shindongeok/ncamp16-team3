package com.izikgram.draw.service;

import java.util.*;

public class DrawGame {
    private List<String> players;
    private List<Integer> results;

    public DrawGame(List<String> players, List<Integer> results) {
        if (players == null || players.isEmpty()) {
            throw new IllegalArgumentException("플레이어 목록이 비어있습니다.");
        }
        if (results == null || results.isEmpty()) {
            throw new IllegalArgumentException("결과 목록이 비어있습니다.");
        }

        // 총 상품 개수 계산
        int totalPrizes = results.stream().mapToInt(Integer::intValue).sum();
        if (totalPrizes > players.size()) {
            throw new IllegalArgumentException("상품 개수(" + totalPrizes +
                    "개)가 플레이어 수(" + players.size() + "명)보다 많습니다.");
        }

        this.players = new ArrayList<>(players);
        this.results = new ArrayList<>(results);
    }

    public Map<String, String> play() {
        Map<String, String> gameResult = new LinkedHashMap<>(); // 순서 유지를 위해 LinkedHashMap 사용
        Random random = new Random();

        // 상품("걸렸다ㅋ") 목록 생성
        List<String> items = new ArrayList<>();

        for (int i = 0; i < results.size(); i++) {
            int prizeCount = results.get(i);

            for (int j = 0; j < prizeCount; j++) {
                items.add("걸렸다ㅋ");
            }
        }

        // 남은 자리는 "살았다"로 채움
        while (items.size() < players.size()) {
            items.add("살았다");
        }

        // 각 플레이어에게 랜덤으로 상품 배정
        Collections.shuffle(items, random);

        for (int i = 0; i < players.size(); i++) {
            gameResult.put(players.get(i), items.get(i));
        }

        return gameResult;
    }
}