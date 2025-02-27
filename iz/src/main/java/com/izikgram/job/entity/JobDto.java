package com.izikgram.job.entity;

import lombok.Data;

@Data
public class JobDto {
    private String id;
    private String companyName;
    private String expirationTimestamp;
    private String url;                 // 채용공고 URL
}
