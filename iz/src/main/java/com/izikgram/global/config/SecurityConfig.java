package com.izikgram.global.config;

import com.izikgram.user.controller.CustomLoginSuccessHandler;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
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
                        .requestMatchers("/", "/login", "/user/findId", "/user/findIdResult", "/user/findPw", "/user/findPwResult", "/user/register", "/user/checkId/**").permitAll() // 로그인 없이 접근 가능
                        .requestMatchers("/css/**", "/js/**", "/images/**", "/static/**").permitAll() // 정적 리소스 허용
                        .anyRequest().authenticated() // 나머지는 로그인 필요
                )
                .formLogin(login -> login
                        .loginPage("/") // 커스텀 로그인 페이지 주소
                        .loginProcessingUrl("/login") // 로그인 처리 엔드포인트
                        .usernameParameter("member_id")
                        .passwordParameter("password")
                        .successHandler(loginSuccessHandler) // 로그인 성공 핸들러 적용
                        .failureHandler((request, response, exception) -> {
                            response.setContentType("application/json;charset=UTF-8");
                            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                            String jsonResponse = "{\"title\": \"로그인 실패!\", \"message\": \"아이디 또는 비밀번호가 올바르지 않습니다.\"}";
                            response.getWriter().write(jsonResponse);
                        })
                        .permitAll()
                )
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.NEVER) // 필요할 때만 세션 설정(자동 생성X), *JWT 사용하면 STATELESS(세션 아예사용X)로 바꿔야함
                        .maximumSessions(1) // 하나의 아이디에 대한 다중 로그인 허용 개수 1개
                        .maxSessionsPreventsLogin(true) // ture : 새로운 로그인 차단, false : 기존 세션 하나 삭제
                )

                .logout(logout -> logout
                        .logoutUrl("/logout") // 로그아웃 URL

                        //이거 사용하면 logout 컨트롤러는 무시됨 그래서 없애도 될 듯 session.invalidate()는
                        //LogoutFilter가 내부적으로 해줌
                        .logoutSuccessUrl("/") // 로그아웃 후 이동할 페이지
                        .deleteCookies("JSESSIONID")
                        .invalidateHttpSession(true) // 세션 삭제
                        .permitAll()
                );

        return http.build();
    }

}
