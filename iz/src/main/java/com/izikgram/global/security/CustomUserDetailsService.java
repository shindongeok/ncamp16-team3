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
        if("DELETED".equals(user.getStatus())) {
            throw new DisabledException("삭제된 계정입니다.");
        }

        Stress stress = userMapper.getUserStress(member_id);

        UserStressDTO userStressDTO = new UserStressDTO(user.getMember_id(), stress.getStress_num());
        log.info("userSTressDTO 객체 : {}", userStressDTO);

        if (user == null) {
            throw new UsernameNotFoundException("User not found: " + member_id);
        }
        return new CustomUserDetails(user, userStressDTO);
    }
}
