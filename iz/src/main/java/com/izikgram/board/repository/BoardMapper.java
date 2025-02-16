package com.izikgram.board.repository;

import com.izikgram.board.entity.Board;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface BoardMapper {

    //게시판 종류
    @Select("select board_name " +
            "from iz_board_type " +
            "where board_type = #{board_type}")
    String getBoardName(@Param("board_type") int board_type);

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
            "WHERE iz.board_type = #{board_type} " +
            "ORDER BY board_id DESC")
    List<Board> selectBoardList(@Param("board_type") int board_type);

    //글작성 하기
    //insertPost 빨간색인데 무시해도 되는거 같아요..
    /*
    DELIMITER $$

    CREATE PROCEDURE insertPost(
            IN p_writer_id VARCHAR(50),
    IN p_title VARCHAR(255),
    IN p_content TEXT,
    IN p_board_type INT
    )
    BEGIN
    -- 조건에 맞는 테이블에 삽입
    IF p_board_type = 1 THEN
    INSERT INTO iz_board01 (writer_id, title, content, board_type)
    VALUES (p_writer_id, p_title, p_content, p_board_type);
    ELSEIF p_board_type = 2 THEN
    INSERT INTO iz_board02 (writer_id, title, content, board_type)
    VALUES (p_writer_id, p_title, p_content, p_board_type);
    END IF;
    END $$

    DELIMITER ;
     */
    @Insert("CALL insertPost(#{writer_id}, #{title}, #{content}, #{board_type})")
    void insertPost(@Param("writer_id") String writer_id, @Param("title") String title, @Param("content") String content,
                    @Param("board_type") int board_type);

    //게시글 상세보기
    @Select("SELECT board_type, board_id, writer_id, title, content, reg_date " +
            "FROM (SELECT b1.board_type, b1.board_id, b1.writer_id, b1.title, b1.content, b1.reg_date " +
            "    FROM iz_board01 b1 " +
            "    WHERE b1.board_type = #{board_type} AND b1.board_id = #{board_id} " +
            "    UNION ALL " +
            "    SELECT b2.board_type, b2.board_id, b2.writer_id, b2.title, b2.content, b2.reg_date " +
            "    FROM iz_board02 b2  " +
            "    WHERE b2.board_type = #{board_type} AND b2.board_id = #{board_id} " +
            ") AS board ")
    Board selectBoard(@Param("board_id") int board_id, @Param("board_type") int board_type);


    //자유게시글 업데이트
    @Update("UPDATE iz_board01 " +
            "SET " +
            "    title = #{title}, " +
            "    content = #{content}, " +
            "    reg_date = current_timestamp " +
            "WHERE " +
            "    board_id = #{board_id} ")
    int updateFreeBoard(@Param("board_id") int board_id,
                          @Param("title") String title,
                          @Param("content")String content);

    //하소연게시글 업데이트
    @Update("UPDATE iz_board02 " +
            "SET " +
            "    title = #{title}, " +
            "    content = #{content}, " +
            "    reg_date = current_timestamp " +
            "WHERE " +
            "    board_id = #{board_id} ")
    int updateDiscontentBoard(@Param("board_id") int board_id,
                                @Param("title") String title,
                                @Param("content")String content);

}
