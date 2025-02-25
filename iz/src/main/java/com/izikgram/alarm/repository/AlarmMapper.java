package com.izikgram.alarm.repository;

import com.izikgram.alarm.entity.AlarmComment;
import com.izikgram.alarm.entity.AlarmDto;
import com.izikgram.alarm.entity.AlarmType;
import org.apache.ibatis.annotations.*;

import java.time.LocalDateTime;
import java.util.Date;
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

    // 공고스크랩하면 iz_alarm_scrap 테이블에 저장
    @Insert("insert into iz_alarm_scrap(member_id, job_rec_id, company_name, expiration_timestamp, alarm_date, content) " +
            "values (#{member_id}, #{job_rec_id}, #{company_name}, #{expiration_timestamp}, now(), #{content})")
    void ScrapSave(@Param("member_id") String member_id,
                   @Param("job_rec_id") String job_rec_id,
                   @Param("company_name") String company_name,
                   @Param("expiration_timestamp") String expiration_timestamp,
                   @Param("content") String content);


//    @Select("select * from iz_alarm_board where member_id=#{member_id} order by alarm_date desc")
//    List<AlarmComment> alarmFindByUser(@Param("member_id") String member_id);

    // 안 읽은것만 조회
    @Select("select * from iz_alarm_board where member_id = #{member_id} and isRead = 0 order by alarm_date desc")
    List<AlarmComment> findAlarmsByUser(@Param("member_id") String member_id);



    @Select("SELECT * FROM (" +
            "    SELECT alarm_id, member_id, alarm_date, isRead, " +
            "           NULL as job_rec_id, NULL as company_name, NULL as expiration_timestamp, " +
            "           board_type, board_id, alarm_type, content " +
            "    FROM iz_alarm_board " +
            "    WHERE member_id = #{member_id} AND isRead = 0 " +
            "    UNION ALL " +
            "    SELECT alarm_id, member_id, " +
            "           alarm_date, isRead, " +
            "           job_rec_id, company_name, expiration_timestamp, " +
            "           NULL as board_type, NULL as board_id, NULL as alarm_type, content " +
            "    FROM iz_alarm_scrap " +
            "    WHERE member_id = #{member_id} AND isRead = 0" +
            ") AS combined " +
            "ORDER BY alarm_date DESC")
    List<AlarmDto> findAllAlarmsByUser(@Param("member_id") String member_id);

    //사용자가 알람을 읽었는지 확인 -> X 누르면 읽음으로 간주
    @Update("UPDATE iz_alarm_board SET isRead = 1 WHERE alarm_id = #{alarm_id}")
    void checkRead(@Param("alarm_id") int alarm_id);


    @Update("UPDATE iz_alarm_scrap SET isRead = 1 WHERE alarm_id = #{alarm_id}")
    void checkScrapRead(@Param("alarm_id") int alarm_id);

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

    // 스크랩알림이 이미 존재 하는지 확인 (읽은 상태 관계없이)
    @Select("SELECT COUNT(*) FROM iz_alarm_scrap WHERE job_rec_id = #{job_rec_id}")
    int countScrapAlarm(@Param("job_rec_id") String job_rec_id);


}
