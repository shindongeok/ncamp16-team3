package com.izikgram.user.entity;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class User {

    @Pattern(regexp = "[a-zA-Z0-9]{6,20}", message = "6~20자 사이의 영문대소문자와 숫자로만 입력해주세요")
    private String member_id;

    @Pattern(regexp = "^(?=.*?[A-Z])(?=.*?[a-z])(?=.*?[0-9]).{8,15}$", message = "비밀번호는 8~15자 사이이며, 영문대소문자와 숫자가 필수입니다")
    private String password;

    @Pattern(regexp = "[가-힣]{2,5}", message = "2~5글자 사이의 한글로 적어주세요")
    private String name;

    @Pattern(regexp = "[0-9]{10,11}", message = "10~11글자 사이의 숫자만 입력해주세요")
    private String phone_num;
    private String nickname;
    private String birth_date;

    @Email(message = "이메일형식으로 적어주세요")
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

    // ACTIVE(default) : 활동중, DELETED : 탈퇴
    private String status;

}
