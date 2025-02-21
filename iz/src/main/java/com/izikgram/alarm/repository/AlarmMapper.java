package com.izikgram.alarm.repository;

import com.izikgram.alarm.entity.AlarmComment;
import com.izikgram.alarm.entity.AlarmType;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface AlarmMapper {

    @Insert("INSERT INTO iz_alarm_board (member_id, board_type, board_id, alarm_date, content, alarm_type) " +
            "VALUES (#{member_id}, #{board_type}, #{board_id}, NOW(), #{content}, #{alarm_type})")
    void save(@Param("member_id") String member_id,
              @Param("board_type") int board_type,
              @Param("board_id") int board_id,
              @Param("content") String content,
              @Param("alarm_type") AlarmType alarm_type);


//    @Select("select * from iz_alarm_board where member_id=#{member_id} order by alarm_date desc")
//    List<AlarmComment> alarmFindByUser(@Param("member_id") String member_id);

    // 안 읽은것만 조회
    @Select("select * from iz_alarm_board where member_id = #{member_id} and isRead = 0 order by alarm_date desc")
    List<AlarmComment> findAlarmsByUser(@Param("member_id") String member_id);


    //사용자가 알람을 읽었는지 확인 -> X 누르면 읽음으로 간주
    @Update("UPDATE iz_alarm_board SET isRead = 1 WHERE alarm_id = #{alarm_id}")
    void checkRead(@Param("alarm_id") int alarm_id);

    // 인기게시글 알림이 이미 존재하는지 체크
    // 보낸 적이 있다면 1리턴 (하지만 삭제하지 않아야함 그래야 보낸 적이 있는지 확인 가능)
//    @Select("SELECT COUNT(*) FROM iz_alarm_board WHERE board_id = #{board_id} AND alarm_type = 'POPULAR'")
//    int checkPopularNotification(int board_id);

//    //인기게시글이면서 읽지 않았다.
//    @Select("SELECT COUNT(*) FROM iz_alarm_board WHERE board_id = #{board_id} AND alarm_type = 'POPULAR' AND isRead = false")
//    int countUnreadPopularAlarm(@Param("board_id") int board_id);

    // 인기게시글 알림이 이미 존재하는지 확인 (읽음 상태 관계없이)
    @Select("SELECT COUNT(*) FROM iz_alarm_board WHERE board_id = #{board_id} AND alarm_type = 'POPULAR'")
    int countPopularAlarm(@Param("board_id") int board_id);
}
