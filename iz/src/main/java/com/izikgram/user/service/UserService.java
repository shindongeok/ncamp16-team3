package com.izikgram.user.service;

import com.izikgram.user.entity.User;
import com.izikgram.user.repository.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;


    public void register(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userMapper.insertUser(user);
        userMapper.insertUserStressInfo(user.getMember_id());
    }

    public boolean userIdCheck(String member_id) {
        int exist = userMapper.existUserCheck(member_id);
        if (exist > 0) {
            return true;
        } else {
            return false;
        }
    }

    public String findUserByPhoneNumber(String phoneNumber) {
        return userMapper.findIdByPhoneNumber(phoneNumber);
    }

    public User getUserInfo(String member_id) {
        return userMapper.getUserInfo(member_id);
    }

    public void deleteUser(String member_id) {
        userMapper.deleteUser(member_id);
    }

    public void updateUserPw(String encodedPassword, String memberId) {
        userMapper.updateUserPw(encodedPassword, memberId);
    }

    public String findId(String name) {
        return userMapper.findIdByName(name);
    }

    public boolean findPassword(String member_id) {
        User userById = userMapper.findUserById(member_id);
        if (userById == null) {
            return false;
        } else {
            return true;
        }
    }

    public void updatePassword(String member_id, String password) {
        userMapper.updateUserPw(password, member_id);
    }

    public void updateUser(User user) {
        userMapper.updateUser(user);
    }
}
