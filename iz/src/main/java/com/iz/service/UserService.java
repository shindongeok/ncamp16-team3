package com.iz.service;

import com.iz.entity.User;
import com.iz.repository.UserMapper;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private HttpSession session;

    public void register(User user) {
        userMapper.insertUser(user);
    }

    public User getUserInfo(String member_id) {
        return userMapper.getUserInfo(member_id);
    }

    public void deleteUser(String member_id) {

        //세션에서 user객체 가져옴
//        User user = (User) session.getAttribute("user");
        userMapper.deleteUser(member_id);

        //탈퇴 후 user 객체를 제거해서 로그아웃됨
//        session.removeAttribute("user");

    }
}
