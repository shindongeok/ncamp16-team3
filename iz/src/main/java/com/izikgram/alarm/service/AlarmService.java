package com.izikgram.alarm.service;

import com.izikgram.alarm.entity.AlarmComment;
import com.izikgram.alarm.entity.AlarmType;
import com.izikgram.alarm.repository.AlarmMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class AlarmService {

    @Autowired
    private AlarmMapper alarmMapper;

    public void save(String member_id, int board_type, int board_id, String title, AlarmType alarm_type) {
        alarmMapper.save(member_id, board_type, board_id, title, alarm_type);
    }


    public List<AlarmComment> findAlarmsByUser(String member_id) {
        return alarmMapper.findAlarmsByUser(member_id);
    }

    // 알람읽음으로 표시 isRead = 1
    public void checkRead(int alarm_id) {
        alarmMapper.checkRead(alarm_id);
    }

    // 이미 인기 게시글에 대한 알림이 있었는지 확인하는 메서드 (읽음 상태 상관없이)
    public boolean hasPopularAlarm(int board_id) {
        // iz_alarm_board에서 해당 board_id와 AlarmType.POPULAR에 대한 알림이 있는지 확인
        return alarmMapper.countPopularAlarm(board_id) > 0;
    }

}
