package com.izikgram.restaurant.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class NaverSearchResponse {
    @JsonProperty("items")
    private List<Restaurant> restaurants;

    @JsonProperty("total")
    private int total;

    private String lastBuildDate;
    private int start;
    private int display;
}