package com.izikgram.main.service;

import com.izikgram.board.entity.Board;
import com.izikgram.main.repository.MainMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class MainService {

    @Autowired
    private MainMapper mainMapper;

    public List<Board> getPopularBoardList() {
        return mainMapper.getPopularBoardList();
    }

    public List<Map<String, Object>> getMonthlyFeeling(String member_id, String date) {
        return mainMapper.getMonthlyFeeling(member_id, date);
    }

}
