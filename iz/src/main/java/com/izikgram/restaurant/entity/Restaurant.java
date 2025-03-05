package com.izikgram.restaurant.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class Restaurant {
    private String title;       // 상호명
    private String link;        // 상세 정보 URL
    private String category;    // 카테고리

    @JsonProperty("description")
    private String description; // 설명

    @JsonProperty("telephone")
    private String telephone;   // 전화번호

    @JsonProperty("address")
    private String address;     // 지번 주소

    @JsonProperty("roadAddress")
    private String roadAddress; // 도로명 주소

    @JsonProperty("mapx")
    private String mapx;        // X 좌표

    @JsonProperty("mapy")
    private String mapy;        // Y 좌표

    // 거리는 API 응답에 포함될 수 있음
    @JsonProperty("distance")
    private int distance;       // 거리 (미터)
}