package com.izikgram.job.entity;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class AlarmJob {

    private int alarm_id;                // 알림 ID
    private String member_id;            // 회원 ID
    private String job_rec_id;           // 채용공고 id
    private String company_name;         // 회사명
    private LocalDateTime expiration_timestamp; // 공고 만료일
    private String alarm_date;           // 알림 전송된 시간

    // 알림 읽음 여부 확인
    private boolean isRead;

}
