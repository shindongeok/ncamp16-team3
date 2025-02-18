package com.izikgram.global.config;

import org.assertj.core.api.Assertions;
import org.jasypt.encryption.pbe.StandardPBEStringEncryptor;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

class JasyptConfigTest extends JasyptConfig {

    @Test
    void mysql_end_dnc () {
        String url = "jdbc:mysql://localhost:3306/project_izikgram?serverTimezone=Asia/Seoul";
        String username = "root";
        String password = "root1234";

        String encryptUrl = jasyptEncrypt(url);
        String encryptUsername = jasyptEncrypt(username);
        String encryptPassword = jasyptEncrypt(password);

        System.out.println("encURL: " + encryptUrl);
        System.out.println("decURL: " + jasyptDecrypt(encryptUrl));
        System.out.println("encUsername: " + encryptUsername);
        System.out.println("encPassword: " + encryptPassword);

        assertThat(url).isEqualTo(jasyptDecrypt(encryptUrl));
        assertThat(username).isEqualTo(jasyptDecrypt(encryptUsername));
        assertThat(password).isEqualTo(jasyptDecrypt(encryptPassword));
    }

    @Test
    void redis_enc_dec() {
        String redisUrl = "redis-11663.c340.ap-northeast-2-1.ec2.redns.redis-cloud.com";
        String redisName = "default";
        String redisPassword = "sF7A92lcx77vcjlmI759EFWnih1wrD0p";

        String encUrl = jasyptEncrypt(redisUrl);
        String encUsername = jasyptEncrypt(redisName);
        String encPassword = jasyptEncrypt(redisPassword);

        System.out.println("encURL: " + encUrl);
        System.out.println("encUsername: " + encUsername);
        System.out.println("encPassword: " + encPassword);

        assertThat(redisUrl).isEqualTo(jasyptDecrypt(encUrl));
        assertThat(redisName).isEqualTo(jasyptDecrypt(encUsername));
        assertThat(redisPassword).isEqualTo(jasyptDecrypt(encPassword));

    }

    @Test
    void coolsms_enc_dec() {
        String coolApi = "NCSXL7EHXNZUBDSR";
        String coolApiSecret = "L8HUWOVQHOYRPATAISHNS5SZD54BRRXL";
        String coolSenderNumber = "01093171040";
        String coolDomain = "https://api.coolsms.co.kr";

        String encUrl = jasyptEncrypt(coolApi);
        String encPassword = jasyptEncrypt(coolApiSecret);
        String encSenderNumber = jasyptEncrypt(coolSenderNumber);
        String encDomain = jasyptEncrypt(coolDomain);

        System.out.println("encURL: " + encUrl);
        System.out.println("encPassword: " + encPassword);
        System.out.println("encSenderNumber: " + encSenderNumber);
        System.out.println("encDomain: " + encDomain);

        assertThat(coolApi).isEqualTo(jasyptDecrypt(encUrl));
        assertThat(coolApiSecret).isEqualTo(jasyptDecrypt(encPassword));
        assertThat(coolSenderNumber).isEqualTo(jasyptDecrypt(encSenderNumber));
        assertThat(coolDomain).isEqualTo(jasyptDecrypt(encDomain));
    }

    private String jasyptEncrypt(String input) { // 암호화
        String key = "izik1234";
        StandardPBEStringEncryptor encryptor = new StandardPBEStringEncryptor();
        encryptor.setAlgorithm("PBEWithMD5AndDES");
        encryptor.setPassword(key);
        return encryptor.encrypt(input);
    }

    private String jasyptDecrypt(String input){ // 복호화
        String key = "izik1234";
        StandardPBEStringEncryptor encryptor = new StandardPBEStringEncryptor();
        encryptor.setAlgorithm("PBEWithMD5AndDES");
        encryptor.setPassword(key);
        return encryptor.decrypt(input);
    }

}