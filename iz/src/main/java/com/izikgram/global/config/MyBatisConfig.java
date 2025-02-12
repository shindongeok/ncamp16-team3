package com.izikgram.global.config;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@MapperScan(basePackages = "com.izikgram.**.repository")
public class MyBatisConfig {
}
