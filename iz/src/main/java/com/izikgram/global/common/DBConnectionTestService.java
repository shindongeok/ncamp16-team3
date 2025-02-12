package com.izikgram.global.common;

import com.izikgram.user.repository.UserMapper;
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
