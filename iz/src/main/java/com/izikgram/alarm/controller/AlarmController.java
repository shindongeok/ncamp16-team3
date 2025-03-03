package com.izikgram.alarm.controller;

import com.izikgram.alarm.entity.AlarmDto;
import com.izikgram.alarm.entity.AlarmType;
import com.izikgram.alarm.service.AlarmService;
import com.izikgram.global.security.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/main")
@RequiredArgsConstructor
public class AlarmController {

    @Autowired
    private final AlarmService alarmService;

    @GetMapping("/alarm")
    public String alarm(@AuthenticationPrincipal CustomUserDetails userDetails, Model model) {
        List<AlarmDto> alarmDtoList = alarmService.findAllAlarmsByUser(userDetails.getUser().getMember_id());
        model.addAttribute("alarmList", alarmDtoList);
        return "main/alarm";
    }

    @PatchMapping("/alarm/{alarm_id}")
    public ResponseEntity<?> checkRead(@RequestBody AlarmDto alarmDto,
                                       @PathVariable int alarm_id) {
        if (alarmDto.getAlarm_type().equals(AlarmType.POPULAR) || alarmDto.getAlarm_type().equals(AlarmType.COMMENT)) {
            alarmService.checkRead(alarm_id);
        } else if(alarmDto.getAlarm_type().equals(AlarmType.SCRAP)) {
            alarmService.checkScrapRead(alarm_id);
        }

        return ResponseEntity.ok().build();
    }

    // JS에서 최신 알림 개수를 가져오기 위한 API
    @GetMapping("/alarm/count")
    @ResponseBody
    public Map<String, Integer> getAlarmCount(@AuthenticationPrincipal CustomUserDetails userDetails) {
        int count = (userDetails != null) ? alarmService.countTotalAlarm(userDetails.getUser().getMember_id()) : 0;

        // 알림 지운 후 count 값 반환
        return Collections.singletonMap("count", count);
    }
}