package com.izikgram.board.entity;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CommentDto {

    private int comment_id;
    private int board_id;
    private String writer_id;
    private String comment_content;
    private LocalDateTime reg_date;
    private String nickname;


}
