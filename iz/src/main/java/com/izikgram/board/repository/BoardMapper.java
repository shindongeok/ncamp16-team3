package com.izikgram.board.repository;

import com.izikgram.board.entity.Board;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface BoardMapper {

    //게시판 종류
    @Select("select board_name " +
            "from iz_board_type " +
            "where board_type = #{board_type}")
    String getBoardName(@Param("board_type") int boardType);

    //게시판 전체 조회
    @Select("SELECT " +
            "    iz.board_type, " +
            "    CASE " +
            "        WHEN iz.board_type = 1 THEN '자유게시판' " +
            "        WHEN iz.board_type = 2 THEN '하소연게시판' " +
            "        ELSE '기타' " +
            "    END AS board_name, " +
            "    CASE " +
            "        WHEN iz.board_type = 1 THEN b1.board_id " +
            "        WHEN iz.board_type = 2 THEN b2.board_id " +
            "        ELSE NULL " +
            "    END AS board_id, " +
            "    CASE " +
            "        WHEN iz.board_type = 1 THEN b1.writer_id " +
            "        WHEN iz.board_type = 2 THEN b2.writer_id " +
            "        ELSE NULL " +
            "    END AS writer_id, " +
            "    CASE " +
            "        WHEN iz.board_type = 1 THEN b1.title " +
            "        WHEN iz.board_type = 2 THEN b2.title " +
            "        ELSE NULL " +
            "    END AS title, " +
            "    CASE " +
            "        WHEN iz.board_type = 1 THEN b1.content " +
            "        WHEN iz.board_type = 2 THEN b2.content " +
            "        ELSE NULL " +
            "    END AS content, " +
            " " +
            "    CASE " +
            "        WHEN iz.board_type = 1 THEN b1.reg_date " +
            "        WHEN iz.board_type = 2 THEN b2.reg_date " +
            "        ELSE NULL " +
            "    END AS reg_date " +
            "FROM iz_board_type iz " +
            "LEFT JOIN iz_board01 b1 ON iz.board_type = b1.board_type " +
            "LEFT JOIN iz_board02 b2 ON iz.board_type = b2.board_type " +
            "WHERE iz.board_type = #{board_type}")
    List<Board> selectBoardList(@Param("board_type") int boardType);


}
