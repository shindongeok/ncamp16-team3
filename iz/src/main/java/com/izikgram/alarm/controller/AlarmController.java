package com.izikgram.alarm.controller;

import com.izikgram.alarm.entity.AlarmComment;
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
        List<AlarmComment> alarmList = alarmService.alarmFindByUser(userDetails.getUser().getMember_id());

        log.info("alarmList : {}", alarmList);
        model.addAttribute("alarmList", alarmList);

        return "main/alarm";
    }

    @DeleteMapping("/alarm")
    public ResponseEntity<?> delete(@RequestBody AlarmComment alarmComment) {
        log.info("삭제할 alarm_id : {}" , alarmComment.getAlarm_id());
        alarmService.delete(alarmComment.getAlarm_id());
        return ResponseEntity.ok().build();
    }
}