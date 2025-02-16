package com.izikgram.user.controller;

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
        String member_Id = authentication.getName(); // 로그인한 사용자 ID 가져오기
        User user = userMapper.findUserById(member_Id); // DB에서 사용자 정보 조회

        if (user != null) {
            log.info("로그인 성공!!!!!: {} -> /main 이동", user.getMember_id());
            log.info("사용자 정보 : {}", user);
            //ajax 써서 필요없을듯..?
//            response.sendRedirect("/main"); // 로그인 성공 후 /main 페이지로 이동
        } else {
            //ajax 써서 필요없을듯..?
//            response.sendRedirect("/"); // 로그인 실패 시 다시 로그인 페이지로
        }
    }
}
