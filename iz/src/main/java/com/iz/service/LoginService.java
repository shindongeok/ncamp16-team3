package com.iz.service;

import com.iz.repository.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class LoginService {

    @Autowired
    private UserMapper userMapper;

    public void login(String member_id, String password) {
        userMapper.login(member_id, password);
    }
}
