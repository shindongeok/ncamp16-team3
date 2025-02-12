package com.iz.service;

import com.iz.repository.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DBConnectionTestService {
    private final UserMapper userMapper;

    public String checkDatabaseConnection() {
        return userMapper.getCurrentTime();
    }
}
