package com.izikgram.board.entity;

import lombok.Data;

@Data
public class Comment {

    private int comment_id;
    private int board_id;
    private String writer_id;
    private String comment_content;
    private String reg_date;

}
