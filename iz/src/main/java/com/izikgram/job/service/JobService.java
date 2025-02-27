package com.izikgram.job.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.izikgram.alarm.service.AlarmService;
import com.izikgram.alarm.service.SseEmitterService;
import com.izikgram.job.entity.AlarmJob;
import com.izikgram.job.entity.Job;
import com.izikgram.job.entity.JobDto;
import com.izikgram.job.repository.JobMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class JobService {
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    @Autowired
    private SseEmitterService sseEmitterService;

    @Autowired
    private AlarmService alarmService;

    @Value("${saramin.api.key}")
    private String apiKey;

    @Autowired
    private JobMapper jobMapper;

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

    public List<Job> searchJobsById(String jobId) {
        try {
            String url = UriComponentsBuilder
                    .fromHttpUrl("https://oapi.saramin.co.kr/job-search")
                    .queryParam("access-key", apiKey)
                    .queryParam("id", jobId)
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

    public List<Job> getDeadlineJobs(List<Job> jobs, int offset) {
        long currentTime = System.currentTimeMillis() / 1000;

        return jobs.stream()
                .filter(job -> {
                    long expirationTime = Long.parseLong(job.getExpirationTimestamp());
                    return expirationTime > currentTime;
                })
                .sorted((a, b) -> {
                    long timeA = Long.parseLong(a.getExpirationTimestamp());
                    long timeB = Long.parseLong(b.getExpirationTimestamp());
                    return Long.compare(timeA, timeB);
                })
                .skip(offset)  // offset만큼 건너뛰기
                .limit(5)
                .collect(Collectors.toList());
    }

    public List<Job> getRecentJobs(List<Job> jobs, int offset) {
        return jobs.stream()
                .sorted((a, b) -> {
                    long timeA = Long.parseLong(a.getPostingTimestamp());
                    long timeB = Long.parseLong(b.getPostingTimestamp());
                    return Long.compare(timeB, timeA);
                })
                .skip(offset)  // offset만큼 건너뛰기
                .limit(5)
                .collect(Collectors.toList());
    }

    public boolean checkIfScraped(String job_rec_id, String member_id) {
        return jobMapper.checkIfScraped(job_rec_id, member_id);
    }

    @Transactional
    public boolean toggleScrap(JobDto jobDto, String memberId) {
        try {
            log.info("jobDTO: {}, memberId: {}", jobDto, memberId);
            //스크랩 여부 확인
            boolean exists = jobMapper.checkIfScraped(jobDto.getId(), memberId);
            log.info("Job exists in scrap: {}", exists);


            if (exists) {
                // 이미 스크랩되어 있으면 삭제
                jobMapper.deleteJobScrap(jobDto, memberId);
                log.info("try안에 jobDTO들어옴?: {}, memberId: {}", jobDto, memberId);

//                // iz_alarm_scarp 테이블에서 읽음으로 표시(알림 다시 안오게 하기 위함)
//                alarmService.checkScrapRead(job.getId(), job.getMemberId());

                return false;
            } else {
                // 스크랩되어 있지 않으면 iz_job_scrap 테이블에 데이터 추가
                jobMapper.addJobScrap(jobDto, memberId);
//                alarmService.ScrapSave(job.getMemberId(), job.getId(), job.getCompanyName(),
//                        LocalDateTime.parse(job.getExpirationTimestamp()));

                // 이미 알림이 왔던 공고인지 확인 후 데이터 저장
                checkScrapAndSendAlarm(jobDto, memberId);

                return true;
            }
        } catch (Exception e) {
            log.error("Error adding job scrap: {}", e.getMessage());
            throw new RuntimeException("Failed to toggle scrap", e);
        }
    }

    private void checkScrapAndSendAlarm(JobDto jobDto, String member_id) {
        // 스크랩한 공고 리스트 반환 - null이면 처음 스크랩하는 것
        AlarmJob alarmScrap = jobMapper.getAlarmScrapList(jobDto.getId(), member_id);
        log.info("AlarmScrap = {}", alarmScrap);

        // 처음 스크랩하는 경우에만 알림 전송
        if(alarmScrap == null) {
            String content = "[" + jobDto.getCompanyName() + "] 의 채용공고를 스크랩 했습니다.";

            // 알림 데이터 저장
            alarmService.ScrapSave(
                    member_id,
                    jobDto.getId(),
                    jobDto.getCompanyName(),
                    jobDto.getExpirationTimestamp(),
                    content,
                    jobDto.getUrl()
            );

            // 실시간 알림 전송 시도 (실패해도 데이터는 저장됨)
            try {
                sseEmitterService.ScrapSend(member_id, content);
            } catch (Exception e) {
                // 실패하더라도 기능에는 영향 없음 - 경고 수준 로그
                log.warn("실시간 알림 전송 실패 (첫 스크랩 알림): {}", e.getMessage());
            }
        } else {
            // 이미 스크랩한 적이 있는 경우 - 알림 없음
            log.debug("이미 스크랩한 공고입니다. 알림을 보내지 않습니다. jobId: {}, memberId: {}",
                    jobDto.getId(), member_id);
        }
    }


    public List<Job> getScrapedJobs(String memberId) {
        // 사용자의 스크랩된 job_rec_id 목록 가져오기
        List<String> scrapedJobIds = jobMapper.getScrapedJobIds(memberId);

        // 스크랩된 job_rec_id가 없으면 빈 리스트 반환
        if (scrapedJobIds.isEmpty()) {
            return new ArrayList<>();
        }

        // 스크랩한 공고 정보를 저장할 리스트
        List<Job> scrapedJobs = new ArrayList<>();

        // 각 job_rec_id에 대해 사람인 API를 통해 정보 가져오기
        for (String jobId : scrapedJobIds) {
            try {
                // searchJobsById 메서드를 활용하여 채용공고 정보 조회
                List<Job> jobDetails = searchJobsById(jobId);

                if (!jobDetails.isEmpty()) {
                    // 채용공고가 존재하면 리스트에 추가
                    scrapedJobs.add(jobDetails.get(0));
                }
            } catch (Exception e) {
                log.error("Error fetching job details for ID {}: {}", jobId, e.getMessage());
                // 에러가 발생해도 다른 채용공고 정보는 계속 조회
            }
        }

        return scrapedJobs;
    }
}