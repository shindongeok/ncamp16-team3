package com.izikgram.draw.service;

import java.util.*;

public class DrawGame {
    private List<String> players;
    private List<Integer> results;

    public DrawGame(List<String> players, List<Integer> results) {
        this.players = players;
        this.results = results;
    }

    public Map<String, String> play() {
        Map<String, String> gameResult = new HashMap<>();
        Random random = new Random();

        List<String> items = new ArrayList<>();
        for (int i = 0; i < results.size(); i++) {
            for (int j = 0; j < results.get(i); j++) {
                items.add("걸렸다ㅋ"); // "상품"만 추가
            }
        }

        // 플레이어 수보다 상품 수가 적거나 많을 수 있기 때문에, 보정이 필요할 경우 처리
        if (items.size() < players.size()) {
            while (items.size() < players.size()) {
                items.add("살았다"); // 상품이 부족할 때 "없음"을 추가
            }
        } else if (items.size() > players.size()) {
            items = items.subList(0, players.size()); // 상품이 많으면 잘라서 플레이어 수만큼만
        }

        // 각 플레이어에게 랜덤으로 상품 배정
        Collections.shuffle(items, random);
        for (int i = 0; i < players.size(); i++) {
            gameResult.put(players.get(i), items.get(i));
        }

        return gameResult;
    }
}
