package com.izikgram.global.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final CustomLoginSuccessHandler loginSuccessHandler;

    public SecurityConfig(CustomLoginSuccessHandler loginSuccessHandler) {
        this.loginSuccessHandler = loginSuccessHandler;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }


    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable()) // CSRF 비활성화 (테스트용)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/", "/login", "/user/findId", "/user/findIdResult", "/user/findPw", "/user/findPwResult", "/user/register")
                        .permitAll() // 로그인 없이 접근 가능
                        .anyRequest().authenticated() // 나머지는 로그인 필요
                )
                .formLogin(login -> login
                        .loginPage("/") // 커스텀 로그인 페이지
                        .loginProcessingUrl("/login") // 로그인 처리 엔드포인트
                        .usernameParameter("member_id")
                        .passwordParameter("password")
                        .successHandler(loginSuccessHandler) // 로그인 성공 핸들러 적용
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutUrl("/logout") // 로그아웃 URL
                        .logoutSuccessUrl("/") // 로그아웃 후 이동할 페이지
                        .invalidateHttpSession(true) // 세션 삭제
                        .permitAll()
                );

        return http.build();
    }

}
