package com.izikgram.alarm.controller;

import com.izikgram.alarm.entity.AlarmComment;
import com.izikgram.alarm.entity.AlarmDto;
import com.izikgram.alarm.entity.AlarmType;
import com.izikgram.alarm.service.AlarmService;
import com.izikgram.global.security.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.annotations.Delete;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@Controller
@RequestMapping("/main")
@RequiredArgsConstructor
public class AlarmController {

    @Autowired
    private final AlarmService alarmService;

    @GetMapping("/alarm")
    public String alarm(@AuthenticationPrincipal CustomUserDetails userDetails, Model model) {

        log.info("member_id : {}", userDetails.getUser().getMember_id());

        //읽지 않은 알람만 가져오기
        //List<AlarmComment> alarmList = alarmService.findAlarmsByUser(userDetails.getUser().getMember_id());

        List<AlarmDto> alarmDtoList = alarmService.findAllAlarmsByUser(userDetails.getUser().getMember_id());

        log.info("alarmList : {}", alarmDtoList);
        model.addAttribute("alarmList", alarmDtoList);

        return "main/alarm";
    }

    @PatchMapping("/alarm/{alarm_id}")
    public ResponseEntity<?> checkRead(@RequestBody AlarmDto alarmDto,
                                       @PathVariable int alarm_id) {
        log.info("Received alarmDto: {}", alarmDto);
        log.info("Alarm type: {}", alarmDto.getAlarm_type());

        if (alarmDto.getAlarm_type().equals(AlarmType.POPULAR) || alarmDto.getAlarm_type().equals(AlarmType.COMMENT)) {
            log.info("삭제할 게시물 alarm_id : {}", alarm_id);
            alarmService.checkRead(alarm_id);
        } else if(alarmDto.getAlarm_type().equals(AlarmType.SCRAP)) {
            log.info("삭제할 스크랩 alarm_id : {}", alarm_id);
            alarmService.checkScrapRead(alarm_id);
        }

        return ResponseEntity.ok().build();
    }


}