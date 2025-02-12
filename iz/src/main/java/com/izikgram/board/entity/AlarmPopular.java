package com.izikgram.board.entity;

import lombok.Data;

@Data
public class AlarmPopular {

    private int alarm_id;
    private String member_id;
    private int board_type;
    private int board_id;
    private String title;
    private String alarm_date;

}
