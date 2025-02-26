package com.izikgram.global.controller;

import com.izikgram.alarm.service.AlarmService;
import com.izikgram.global.security.CustomUserDetails;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice
public class GlobalController {

    private AlarmService alarmService;

    public GlobalController(AlarmService alarmService) {
        this.alarmService = alarmService;
    }

    @ModelAttribute("alarmCount")
    public int countTotalAlarm(@AuthenticationPrincipal CustomUserDetails userDetails) {
        if (userDetails == null) {
            return 0;
        }

        return alarmService.countTotalAlarm(userDetails.getUser().getMember_id());
    }

}
