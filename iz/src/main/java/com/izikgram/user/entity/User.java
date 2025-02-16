package com.izikgram.user.entity;

import lombok.Data;

@Data
public class User {

    private String member_id;
    private String password;
    private String name;
    private String phone_num;
    private String nickname;
    private String birth_date;
    private String email;
    private int payday;
    private String gender;
    private String start_time;
    private String lunch_time;
    private String end_time;
    private String intro;

    // 1차 근무지/지역조건
    private String loc_mod;
    // 상위 산업/업종 코드
    private String ind_cd;
    // 학력조건
    private String edu_lv;



}
