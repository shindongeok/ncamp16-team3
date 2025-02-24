package com.izikgram.job.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.izikgram.job.entity.Job;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class JobService {
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    @Value("${saramin.api.key}")
    private String apiKey;

    public List<Job> searchJobs(String locMcd, String indCd, String eduLv) {
        try {
            String url = UriComponentsBuilder
                    .fromHttpUrl("https://oapi.saramin.co.kr/job-search")
                    .queryParam("access-key", apiKey)
                    .queryParam("loc_cd", locMcd)
                    .queryParam("ind_cd", indCd)
                    .queryParam("edu_lv", eduLv)
                    .queryParam("count", 50)  // 한 번에 가져올 결과 수 조정
                    .build()
                    .encode()
                    .toUriString();

            ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
            JsonNode rootNode = objectMapper.readTree(response.getBody());
            JsonNode jobsNode = rootNode.path("jobs").path("job");

            List<Job> jobs = new ArrayList<>();
            jobsNode.forEach(jobNode -> {
                Job job = new Job();
                job.setId(jobNode.path("id").asText());
                job.setUrl(jobNode.path("url").asText());

                // 회사 정보
                JsonNode company = jobNode.path("company").path("detail");
                job.setCompanyName(company.path("name").asText());
                job.setCompanyUrl(company.path("href").asText());

                // 포지션 정보
                JsonNode position = jobNode.path("position");
                job.setTitle(position.path("title").asText());
                job.setIndustryCode(position.path("industry").path("code").asText());
                job.setIndustryName(position.path("industry").path("name").asText());
                job.setLocationCode(position.path("location").path("code").asText());
                job.setLocationName(position.path("location").path("name").asText());
                job.setLocationName(
                        position.path("location").path("name").asText()
                                .replace(" &gt; ", " ")
                                .replace(",", ", ")
                                .replace("서울전체", "전체")
                );

                // 경력/학력 정보
                JsonNode expLevel = position.path("experience-level");
                job.setExperienceLevelCode(expLevel.path("code").asText());
                job.setExperienceMin(expLevel.path("min").asInt());
                job.setExperienceMax(expLevel.path("max").asInt());
                job.setEducationLevel(position.path("required-education-level").path("name").asText());

                // 급여 정보
                JsonNode salary = jobNode.path("salary");
                job.setSalaryCode(salary.path("code").asText());
                job.setSalaryName(salary.path("name").asText());

                // timestamp 필드 설정
                job.setPostingTimestamp(jobNode.path("posting-timestamp").asText());
                job.setExpirationTimestamp(jobNode.path("expiration-timestamp").asText());

                // 날짜 정보
                job.setPostingDate(jobNode.path("posting-date").asText());
                job.setExpirationDate(jobNode.path("expiration-date").asText());

                jobs.add(job);
            });

            return jobs;
        } catch (Exception e) {
            throw new RuntimeException("Failed to fetch jobs from Saramin API", e);
        }
    }

    // 마감이 임박한 순으로 5개 정렬
    public List<Job> getDeadlineJobs(List<Job> jobs) {
        long currentTime = System.currentTimeMillis() / 1000; // 현재 시간을 Unix timestamp로 변환

        return jobs.stream()
                .filter(job -> {
                    // 문자열을 long으로 변환
                    long expirationTime = Long.parseLong(job.getExpirationTimestamp());
                    return expirationTime > currentTime; // 아직 마감되지 않은 공고만 필터링
                })
                .sorted((a, b) -> {
                    long timeA = Long.parseLong(a.getExpirationTimestamp());
                    long timeB = Long.parseLong(b.getExpirationTimestamp());
                    return Long.compare(timeA, timeB); // 마감이 빠른 순으로 정렬
                })
                .limit(5)
                .collect(Collectors.toList());
    }

    // 최근 등록 순으로 5개 정렬
    public List<Job> getRecentJobs(List<Job> jobs) {
        return jobs.stream()
                .sorted((a, b) -> {
                    long timeA = Long.parseLong(a.getPostingTimestamp());
                    long timeB = Long.parseLong(b.getPostingTimestamp());
                    return Long.compare(timeB, timeA); // 최신 등록 순으로 정렬 (내림차순)
                })
                .limit(5)
                .collect(Collectors.toList());
    }
}