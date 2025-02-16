package com.izikgram.user.service;

import com.izikgram.global.config.CustomUserDetails;
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
        User user = userMapper.findUserById(member_id);
        if (user == null) {
            throw new UsernameNotFoundException("User not found: " + member_id);
        }

        // return org.springframework.security.core.userdetails.User
        //         .withUsername(user.getMember_id())
        //         .password("{noop}" + user.getPassword())
        //         .build();

        return new CustomUserDetails(user);
    }
}
