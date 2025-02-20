package com.izikgram.alarm.service;

import com.izikgram.alarm.entity.AlarmComment;
import com.izikgram.alarm.repository.AlarmMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class AlarmService {

    @Autowired
    private AlarmMapper alarmMapper;

    public void save(String member_id, int board_type, int board_id, String title) {
        alarmMapper.save(member_id, board_type, board_id, title);
    }

    public List<AlarmComment> alarmFindByUser(String member_id) {
        return alarmMapper.alarmFindByUser(member_id);
    }

    public void delete(int alarm_id) {
        alarmMapper.delete(alarm_id);
    }

}
