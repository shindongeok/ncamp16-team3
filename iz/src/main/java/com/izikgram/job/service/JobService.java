package com.izikgram.job.service;

import com.izikgram.job.repository.JobMapper;
import com.izikgram.user.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class JobService {

    @Autowired
    private JobMapper jobMapper;

    public User getUserById(String member_id) {
        return jobMapper.getUserById(member_id);

    }
}
