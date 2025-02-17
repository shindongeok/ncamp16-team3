package com.izikgram.global.security;

import com.izikgram.user.entity.User;
import com.izikgram.user.repository.UserMapper;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserMapper userMapper;

    public CustomUserDetailsService(UserMapper userMapper) {
        this.userMapper = userMapper;
    }

    @Override
    public UserDetails loadUserByUsername(String member_id) throws UsernameNotFoundException {
        User user = userMapper.getUserInfo(member_id);
        if (user == null) {
            throw new UsernameNotFoundException("User not found: " + member_id);
        }
        return new CustomUserDetails(user);
    }
}
