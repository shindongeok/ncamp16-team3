package com.izikgram.job.entity;

import lombok.Data;

@Data
public class Job {

    private String member_id; // 회원 ID
    private String job_rec_id; // 채용공고 번호
    private String title; // 채용공고 제목
    private String company; // 회사명
    private String loc_mcd; // 근무지역
    private String ind_cd; // 업종
    private String edu_lv; // 학력요건
    private String deadline; // 마감일
}
