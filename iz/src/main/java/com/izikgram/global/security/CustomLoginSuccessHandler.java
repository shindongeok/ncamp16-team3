package com.izikgram.global.security;

import com.izikgram.user.entity.Stress;
import com.izikgram.user.entity.User;
import com.izikgram.user.repository.UserMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Slf4j
@Component
public class CustomLoginSuccessHandler implements AuthenticationSuccessHandler {

    private final UserMapper userMapper;

    public CustomLoginSuccessHandler(UserMapper userMapper) {
        this.userMapper = userMapper;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {
        try {
            String member_id = authentication.getName(); // 로그인한 사용자 ID 가져오기
            User loginuser = userMapper.getUserInfo(member_id); // DB에서 사용자 정보 조회
            Stress userStressDTO = userMapper.getUserStress(member_id); // member_id + stress 지수

            // userStressDTO가 null인 경우 처리
            if (userStressDTO == null) {
                log.warn("스트레스 정보가 없습니다. 기본값을 설정합니다.");
                // 기본값 설정 (예: 0으로 설정)
                userStressDTO = new Stress();
                userStressDTO.setStress_num(0); // 기본값을 0으로 설정
            }

            log.info("로그인 성공!!!!!: {} -> /main 이동", loginuser.getMember_id());
            log.info("사용자 정보 : {}", loginuser);
            log.info("스트레스 지수 = {}", userStressDTO.getStress_num());
            log.info("userStressDTO 정보 : {}", userStressDTO);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
