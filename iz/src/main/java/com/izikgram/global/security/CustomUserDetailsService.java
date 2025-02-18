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

@Slf4j
@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserMapper userMapper;

    public CustomUserDetailsService(UserMapper userMapper) {
        this.userMapper = userMapper;
    }

    @Override
    public UserDetails loadUserByUsername(String member_id) throws UsernameNotFoundException {

        User user = userMapper.getUserInfo(member_id);

        if(user == null) {
            throw new UsernameNotFoundException("회원이 아닙니다.");
        }

        if("DELETED".equals(user.getStatus())) {
            throw new DisabledException("삭제된 계정입니다.");
        }

        User loginUser = userMapper.login(member_id, user.getPassword());
        if(loginUser == null) {
            throw new UsernameNotFoundException("비밀번호가 틀렸습니다.");
        }

        Stress stress = userMapper.getUserStress(member_id);

        // stress가 null일 경우 처리 (기본값 설정)
        if (stress == null) {
            log.warn("스트레스 정보가 없습니다. 기본값을 설정합니다.");
            stress = new Stress();
            stress.setStress_num(0);  // 기본값을 0으로 설정
        }

        UserStressDTO userStressDTO = new UserStressDTO(user.getMember_id(), stress.getStress_num());
        log.info("userStressDTO 객체 : {}", userStressDTO);

        return new CustomUserDetails(user, userStressDTO);
    }
}
