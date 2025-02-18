package com.izikgram.global.config;

import com.ulisesbocchio.jasyptspringboot.annotation.EnableEncryptableProperties;
import org.jasypt.encryption.StringEncryptor;
import org.jasypt.encryption.pbe.PooledPBEStringEncryptor;
import org.jasypt.encryption.pbe.config.SimpleStringPBEConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableEncryptableProperties
public class JasyptConfig {

    @Value("${jasypt.encryptor.password}")
    private String encryptKey;

    @Bean("jasyptStringEncryptor")
    public StringEncryptor stringEncryptor() {
        PooledPBEStringEncryptor encryptor = new PooledPBEStringEncryptor(); // 기본 jasypt의 String 암호화 객체
        SimpleStringPBEConfig config = new SimpleStringPBEConfig(); // 기본 jasypt의 String config 객체

        config.setPassword(encryptKey); // 암호화 key
        config.setAlgorithm("PBEWithMD5AndDES"); // 암호화 알고리즘
        config.setKeyObtentionIterations("1000");   // 반복할 해싱 회수 default
        config.setPoolSize("1");    // 인스턴스 pool default
        config.setSaltGeneratorClassName("org.jasypt.salt.RandomSaltGenerator");    // salt 생성 default 클래스
        config.setStringOutputType("base64");   // 인코딩 방식
        encryptor.setConfig(config);
        return encryptor;
    }
}
