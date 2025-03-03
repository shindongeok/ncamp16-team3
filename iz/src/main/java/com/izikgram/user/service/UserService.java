package com.izikgram.user.service;

import com.izikgram.main.repository.MainMapper;
import com.izikgram.user.entity.User;
import com.izikgram.user.repository.UserMapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private MainMapper mainMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;


    public void register(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userMapper.insertUser(user);
    }

    public boolean userIdCheck(String member_id) {
        int exist = userMapper.existUserCheck(member_id);
        if (exist > 0) {
            return true;
        } else {
            return false;
        }
    }

    public User findUserFromFindId(String name, String phoneNumber) {
        return userMapper.findUserFromFindId(name, phoneNumber);
    }

    public User findUserFromFindPw(String name, String phoneNumber) {
        return userMapper.findUserFromFindPw(name, phoneNumber);
    }

    public Integer getStressNum(String member_id) {  // int -> Integer로 변경
        Integer stressNum = mainMapper.getStressNum(member_id);
        return stressNum;  // null이 반환될 수 있음
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

    public void updateUserTime(String memberId, String startTime, String lunchTime, String endTime) {
        userMapper.updateUserTime(memberId, startTime, lunchTime, endTime);
    }
}
