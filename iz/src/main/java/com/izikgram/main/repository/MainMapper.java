package com.izikgram.main.repository;

import com.izikgram.board.entity.Board;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

@Mapper
public interface MainMapper {

    @Select("SELECT DISTINCT result.board_type, result.title, result.board_id, result.like_count " +
            "FROM ( " +
            "    SELECT b1.board_type, b1.title, b1.board_id, p.like_count " +
            "    FROM ( " +
            "        SELECT 1 as board_type, title, board_id " +
            "        FROM iz_board01 " +
            "        UNION ALL " +
            "        SELECT 2 as board_type, title, board_id " +
            "        FROM iz_board02 " +
            "    ) b1 " +
            "    JOIN ( " +
            "        SELECT board_type as pop_board_type, board_id as pop_board_id, like_count " +
            "        FROM iz_board_popular " +
            "        ORDER BY like_count DESC " +
            "        LIMIT 3 " +
            "    ) p " +
            "    ON b1.board_type = p.pop_board_type AND b1.board_id = p.pop_board_id " +
            ") result " +
            "ORDER BY like_count DESC")
    List<Board> getPopularBoardList();

    @Select("SELECT feeling_num, date FROM iz_member_stress_info " +
            "WHERE member_id = #{member_id} " +
            "AND DATE_FORMAT(date, '%Y-%m') = #{date}")
    List<Map<String, Object>> getMonthlyFeeling(
            @Param("member_id") String member_id,
            @Param("date") String date
    );

    @Select("SELECT payday FROM iz_member WHERE member_id = #{member_id}")
    int getPayday(@Param("member_id") String member_id);

    @Select("SELECT start_time FROM iz_member WHERE member_id = #{member_id}")
    String getStartTime(@Param("member_id") String member_id);

    @Select("SELECT lunch_time FROM iz_member WHERE member_id = #{member_id}")
    String getLunchTime(@Param("member_id") String member_id);

    @Select("SELECT end_time FROM iz_member WHERE member_id = #{member_id}")
    String getEndTime(@Param("member_id") String member_id);

    @Select("SELECT stress_num FROM iz_member_stress_info " +
            "WHERE member_id = #{member_id} " +
            "AND DATE_FORMAT(date, '%Y-%m-%d') = DATE_FORMAT(NOW(), '%Y-%m-%d')")
    int getStressNum(@Param("member_id") String member_id);
}
