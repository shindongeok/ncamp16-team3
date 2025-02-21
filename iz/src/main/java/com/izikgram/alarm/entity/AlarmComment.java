package com.izikgram.alarm.entity;

import lombok.Data;

import java.util.Date;

@Data
public class AlarmComment {

    private int alarm_id;
    private String member_id;
    private int board_type;
    private int board_id;
    private Date alarm_date;
    private String content;

    // COMMENT, POPULAR 구분하기 위함
    private AlarmType alarm_type;

    // 읽음 여부
    private boolean isRead;

}
