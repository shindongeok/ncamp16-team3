package com.izikgram.board.repository;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface CommentMapper {

    @Select("SELECT writer_id FROM iz_board01 WHERE board_id = #{board_id}")
    String findWriterByBoardId(@Param("board_id") int board_id);

    @Insert("INSERT INTO iz_board01_comment (board_id, writer_id, comment_content, reg_date) " +
            "VALUES (#{board_id}, #{writer_id}, #{comment_content}, NOW())")
    void addComment(int board_id, String writer_id, String comment_content);

}
