package com.izikgram.alarm.repository;

import com.izikgram.alarm.entity.AlarmComment;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface AlarmMapper {

    @Insert("insert into iz_alarm_board (member_id, board_type, board_id, alarm_date, content) " +
            "values(#{member_id}, #{board_type}, #{board_id}, now(), #{content})")
    void save(@Param("member_id") String member_id, @Param("board_type") int board_type,@Param("board_id") int board_id, @Param("content") String content);

    @Select("select * from iz_alarm_board where member_id=#{member_id} order by alarm_date desc")
    List<AlarmComment> alarmFindByUser(@Param("member_id") String member_id);

    @Delete("delete from iz_alarm_board where alarm_id=#{alarm_id}")
    void delete(@Param("alarm_id") int alarm_id);

}
