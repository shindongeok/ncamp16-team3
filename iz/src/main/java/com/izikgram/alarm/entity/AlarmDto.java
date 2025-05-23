package com.izikgram.alarm.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class AlarmDto {
    private int alarm_id;
    private String member_id;
    private String content;
    private LocalDateTime alarm_date;
    private boolean isRead;

    // 스크랩 알림 전용 필드
    private String job_rec_id;
    private String company_name;
    private String expiration_timestamp;

    // 게시판 알림 전용 필드
    private Integer board_type;
    private Integer board_id;

    @JsonProperty("alarm_type")
    private AlarmType alarm_type;

    private String url;
}