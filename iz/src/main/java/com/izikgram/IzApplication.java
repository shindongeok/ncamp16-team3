package com.izikgram;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling // 스케줄러 기능 활성화 (3일전 공고 알림)
public class IzApplication {

	public static void main(String[] args) {
		SpringApplication.run(IzApplication.class, args);
	}

}
