package com.izikgram.global.security;

import com.izikgram.user.entity.Stress;
import com.izikgram.user.entity.User;
import com.izikgram.user.repository.UserMapper;
import com.izikgram.user.dto.UserStressDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserMapper userMapper;

    public CustomUserDetailsService(UserMapper userMapper) {
        this.userMapper = userMapper;
    }

    @Override
    public UserDetails loadUserByUsername(String member_id) throws UsernameNotFoundException {

        //유저 데이터 담기
        User user = userMapper.getUserInfo(member_id);

        //유저가 없으면 회원 아닙니다.
        if(user == null) {
            throw new UsernameNotFoundException("회원이 아닙니다.");
        }

        // 탈퇴한 회원이면 삭제된 계정
        if("DELETED".equals(user.getStatus())) {
            throw new DisabledException("삭제된 계정입니다.");
        }

        // 회원의 스트레스 값 가져오기
        List<Stress> stressList = userMapper.getUserStress(member_id);

        // 스트레스 값이 없으면 기본값 0 설정
        Stress stress = new Stress();
        if (!stressList.isEmpty()) {
            stress = stressList.get(0);  // 최신 데이터 사용
        }
        stress.setStress_num(stressList.isEmpty() ? 0 : stress.getStress_num());

        return new CustomUserDetails(user, stress);
    }
}
