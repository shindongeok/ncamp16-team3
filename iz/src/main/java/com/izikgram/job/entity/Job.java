package com.izikgram.job.entity;

import lombok.Data;

@Data
public class Job {
    private String id;                  // 채용공고 번호 (기존 job_rec_id)
    private String memberId;            // 회원 ID
    private String url;                 // 채용공고 URL

    // 회사 정보
    private String companyName;         // 회사명
    private String companyUrl;          // 회사 상세정보 URL

    // 포지션 정보
    private String title;               // 채용공고 제목
    private String industryCode;        // 업종 코드
    private String industryName;        // 업종명
    private String locationCode;        // 근무지역 코드
    private String locationName;        // 근무지역명
    private String jobType;             // 고용형태
    private String experienceLevelCode; // 경력 코드
    private Integer experienceMin;      // 최소 경력
    private Integer experienceMax;      // 최대 경력
    private String educationLevel;      // 학력요건

    // 날짜 정보
    private String postingTimestamp;    // Unix timestamp 문자열
    private String expirationTimestamp;
    private String postingDate;         // 등록일
    private String expirationDate;      // 마감일
}
