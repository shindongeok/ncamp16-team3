package com.izikgram.job.entity;

import lombok.Data;

@Data
public class AlarmJob {

    private int alarm_id;
    private String member_id;
    private String job_rec_id;
    private String company_name;
    private String expiration_timestamp;
    private String alarm_date;

}
