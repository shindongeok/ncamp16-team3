package com.izikgram.global.config;

import org.assertj.core.api.Assertions;
import org.jasypt.encryption.pbe.StandardPBEStringEncryptor;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

class JasyptConfigTest extends JasyptConfig {

    @Test
    void mysql_end_dnc () {
        String url = "";
        String username = "";
        String password = "";

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
        String redisUrl = "";
        String redisName = "";
        String redisPassword = "";

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
        String coolApi = "";
        String coolApiSecret = "";
        String coolSenderNumber = "";
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

    @Test
    void saramin() {
        String saraminApi = "";
        String saraminDomain = "https://oapi.saramin.co.kr/job-search";

        String encUrl = jasyptEncrypt(saraminApi);
        String encPassword = jasyptEncrypt(saraminDomain);

        System.out.println("saraminApi: " + encUrl);
        System.out.println("saraminDomain: " + encPassword);

        assertThat(saraminApi).isEqualTo(jasyptDecrypt(encUrl));
        assertThat(saraminDomain).isEqualTo(jasyptDecrypt(encPassword));
    }

    private String jasyptEncrypt(String input) { // 암호화
        String key = "";
        StandardPBEStringEncryptor encryptor = new StandardPBEStringEncryptor();
        encryptor.setAlgorithm("PBEWithMD5AndDES");
        encryptor.setPassword(key);
        return encryptor.encrypt(input);
    }

    private String jasyptDecrypt(String input){ // 복호화
        String key = "";
        StandardPBEStringEncryptor encryptor = new StandardPBEStringEncryptor();
        encryptor.setAlgorithm("PBEWithMD5AndDES");
        encryptor.setPassword(key);
        return encryptor.decrypt(input);
    }

}