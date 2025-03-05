package com.izikgram.restaurant.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import java.util.List;

public class ReverseGeocodeResponse {
    @JsonProperty("status")
    private Status status;

    @JsonProperty("results")
    private List<GeocodeResult> results;

    @Data
    public static class Status {
        @JsonProperty("code")
        private int code;

        @JsonProperty("name")
        private String name;

        @JsonProperty("message")
        private String message;
    }

    @Data
    public static class GeocodeResult {
        @JsonProperty("name")
        private String name;

        @JsonProperty("region")
        private Region region;

        @JsonProperty("land")
        private Land land;
    }

    @Data
    public static class Region {
        @JsonProperty("area0")
        private Area area0;

        @JsonProperty("area1")
        private Area area1;

        @JsonProperty("area2")
        private Area area2;

        @JsonProperty("area3")
        private Area area3;
    }

    @Data
    public static class Area {
        @JsonProperty("name")
        private String name;
    }

    @Data
    public static class Land {
        @JsonProperty("type")
        private String type;

        @JsonProperty("number1")
        private String number1;

        @JsonProperty("number2")
        private String number2;

        @JsonProperty("addition0")
        private Addition addition0;
    }

    @Data
    public static class Addition {
        @JsonProperty("type")
        private String type;

        @JsonProperty("value")
        private String value;
    }
}