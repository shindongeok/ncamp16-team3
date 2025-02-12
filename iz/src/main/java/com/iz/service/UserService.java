package com.iz.service;

import com.iz.entity.User;
import com.iz.repository.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    @Autowired
    private UserMapper userMapper;

    public void register(User user) {
        userMapper.insertUser(user);
    }

    public User getUserInfo(String member_id) {
        return userMapper.getUserInfo(member_id);
    }


}
