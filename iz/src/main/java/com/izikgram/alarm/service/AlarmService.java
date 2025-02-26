package com.izikgram.alarm.service;

import com.izikgram.alarm.entity.AlarmComment;
import com.izikgram.alarm.entity.AlarmDto;
import com.izikgram.alarm.entity.AlarmType;
import com.izikgram.alarm.repository.AlarmMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;


@Service
public class AlarmService {

    @Autowired
    private AlarmMapper alarmMapper;

    public void save(String member_id, int board_type, int board_id, String title, AlarmType alarm_type) {
        alarmMapper.save(member_id, board_type, board_id, title, alarm_type);
    }

//    public void ScrapSave(String member_id, String job_rec_id, String company_name, long expiration_timestamp) {
//
//        // json 초단위로 받아서 1561820399 → 2019-06-29T23:59:59+0900 변환해줘야함
//        Date expirationDate  = new Date(expiration_timestamp * 1000);
//
//        alarmMapper.ScrapSave(member_id, job_rec_id, company_name, expirationDate);
//    }

    public void ScrapSave(String member_id, String job_rec_id, String company_name, String expiration_timestamp, String content) {
        alarmMapper.ScrapSave(member_id, job_rec_id, company_name, expiration_timestamp, content);
    }

    public void ScrapExpirationSave(String member_id, String job_rec_id, String company_name, String expiration_timestamp, String content) {
        alarmMapper.ScrapExpirationSave(member_id, job_rec_id, company_name, expiration_timestamp, content);
    }


//    public List<AlarmComment> findAlarmsByUser(String member_id) {
//        return alarmMapper.findAlarmsByUser(member_id);
//    }
    public List<AlarmDto> findAllAlarmsByUser(String member_id) {
        return alarmMapper.findAllAlarmsByUser(member_id);
    }

    // 알람읽음으로 표시 isRead = 1
    public void checkRead(int alarm_id) {
        alarmMapper.checkRead(alarm_id);
    }

    public void checkScrapRead(int alarm_id) {
        alarmMapper.checkScrapRead(alarm_id);
    }

    // 이미 인기 게시글에 대한 알림이 있었는지 확인하는 메서드 (읽음 상태 상관없이)
    public boolean hasPopularAlarm(int board_id) {
        // iz_alarm_board에서 해당 board_id와 AlarmType.POPULAR에 대한 알림이 있는지 확인
        return alarmMapper.countPopularAlarm(board_id) > 0;
    }

    public boolean hasScarpAlarm(String job_rec_id) {
        return alarmMapper.countScrapAlarm(job_rec_id) > 0;
    }

}
