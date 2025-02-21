package com.izikgram.job.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@Service
@RequiredArgsConstructor
public class JobService {
    private final RestTemplate restTemplate;

    @Value("${saramin.api.key}")
    private String apiKey;

    public String searchJobs(String loc_mcd, String ind_cd, String edu_lv) {
        try {
            String url = UriComponentsBuilder
                    .fromHttpUrl("https://oapi.saramin.co.kr/job-search")
                    .queryParam("access-key", apiKey)
                    .queryParam("loc_cd", loc_mcd)
                    .queryParam("ind_cd", ind_cd)
                    .queryParam("edu_lv", edu_lv)
                    .build()
                    .encode()
                    .toUriString();

            return restTemplate.getForObject(url, String.class);
        } catch (Exception e) {
            e.printStackTrace();
            return "Error: " + e.getMessage();
        }
    }
}