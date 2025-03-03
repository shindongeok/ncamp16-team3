package com.izikgram.alarm.repository;

import com.izikgram.alarm.entity.AlarmDto;
import com.izikgram.alarm.entity.AlarmType;
import org.apache.ibatis.annotations.*;
import java.util.List;

@Mapper
public interface AlarmMapper {

    // 인기 게시글 됐을 때 알림
    @Insert("INSERT INTO iz_alarm_board (member_id, board_type, board_id, alarm_date, content, alarm_type) " +
            "VALUES (#{member_id}, #{board_type}, #{board_id}, NOW(), #{content}, #{alarm_type})")
    void save(@Param("member_id") String member_id,
              @Param("board_type") int board_type,
              @Param("board_id") int board_id,
              @Param("content") String content,
              @Param("alarm_type") AlarmType alarm_type);

    // 공고스크랩하면 iz_alarm_scrap 테이블에 저장
    @Insert("insert into iz_alarm_scrap(member_id, job_rec_id, company_name, expiration_timestamp, alarm_date, content, url) " +
            "values (#{member_id}, #{job_rec_id}, #{company_name}, #{expiration_timestamp}, now(), #{content}, #{url})")
    void ScrapSave(@Param("member_id") String member_id,
                   @Param("job_rec_id") String job_rec_id,
                   @Param("company_name") String company_name,
                   @Param("expiration_timestamp") String expiration_timestamp,
                   @Param("content") String content,
                   @Param("url") String url);

    // 스크랩한 공고마감 3일 전 알림
    @Insert("insert into iz_alarm_scrap(member_id, job_rec_id, company_name, expiration_timestamp, alarm_date, content) " +
            "values (#{member_id}, #{job_rec_id}, #{company_name}, #{expiration_timestamp}, now(), #{content})")
    void ScrapExpirationSave(@Param("member_id") String member_id,
                             @Param("job_rec_id") String job_rec_id,
                             @Param("company_name") String company_name,
                             @Param("expiration_timestamp") String expiration_timestamp,
                             @Param("content") String content);

    @Select("SELECT * FROM iz_alarm_scrap a " +
            "join iz_job_scrap b " +
            "on a.member_id = b.member_id and a.job_rec_id = b.job_rec_id " +
            "WHERE expiration_timestamp BETWEEN NOW() AND DATE_ADD(NOW(), INTERVAL 3 DAY) and a.content like '%.' and isRead = 1")
    List<AlarmDto> findExpiringScrapJobs();

    @Select("SELECT * FROM (" +
            "    SELECT alarm_id, member_id, alarm_date, isRead, " +
            "           NULL as job_rec_id, NULL as company_name, NULL as expiration_timestamp, " +
            "           board_type, board_id, alarm_type, content, NULL as url " +
            "    FROM iz_alarm_board " +
            "    WHERE member_id = #{member_id} AND isRead = 0 " +
            "    UNION ALL " +
            "    SELECT alarm_id, member_id, " +
            "           alarm_date, isRead, " +
            "           job_rec_id, company_name, expiration_timestamp, " +
            "           NULL as board_type, NULL as board_id, NULL as alarm_type, content, url " +
            "    FROM iz_alarm_scrap " +
            "    WHERE member_id = #{member_id} AND isRead = 0" +
            ") AS combined " +
            "ORDER BY " +
            "    alarm_date DESC")
    List<AlarmDto> findAllAlarmsByUser(@Param("member_id") String member_id);

    //사용자가 알람을 읽었는지 확인 -> X 누르면 읽음으로 간주
    @Update("UPDATE iz_alarm_board SET isRead = 1 WHERE alarm_id = #{alarm_id}")
    void checkRead(@Param("alarm_id") int alarm_id);


    @Update("UPDATE iz_alarm_scrap SET isRead = 1 WHERE alarm_id = #{alarm_id}")
    void checkScrapRead(@Param("alarm_id") int alarm_id);

    // 인기게시글 알림이 이미 존재하는지 확인 (읽음 상태 관계없이)
    @Select("SELECT COUNT(*) FROM iz_alarm_board WHERE board_id = #{board_id} AND alarm_type = 'POPULAR'")
    int countPopularAlarm(@Param("board_id") int board_id);

//    // 스크랩알림이 이미 존재 하는지 확인 (읽은 상태 관계없이)
//    @Select("SELECT COUNT(*) FROM iz_alarm_scrap WHERE job_rec_id = #{job_rec_id}")
//    int countScrapAlarm(@Param("job_rec_id") String job_rec_id);

    // 받은 알림 카운트
    @Select("SELECT COUNT(*) FROM ( " +
            "SELECT alarm_id FROM iz_alarm_board " +
            "WHERE member_id = #{member_id} AND isRead = 0 UNION ALL SELECT alarm_id FROM iz_alarm_scrap WHERE member_id = #{member_id} AND isRead = 0 )AS combined")
    int countTotalAlarm(@Param("member_id") String member_id);
}
