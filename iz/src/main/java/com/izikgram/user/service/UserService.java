package com.izikgram.user.service;

import com.izikgram.user.entity.User;
import com.izikgram.user.repository.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    @Autowired
    private UserMapper userMapper;

    public void register(User user) {
        userMapper.insertUser(user);
    }

    public User getUserById(String member_id) {
        return userMapper.findUserById(member_id);
    }
}
