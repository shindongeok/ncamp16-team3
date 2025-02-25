package com.izikgram.job.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class JobDto {

    private String id;

    private String companyName;

    private String expirationTimestamp;


}
