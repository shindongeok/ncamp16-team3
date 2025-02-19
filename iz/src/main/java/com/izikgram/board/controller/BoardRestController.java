package com.izikgram.board.controller;


import com.izikgram.board.service.BoardService;
import com.izikgram.global.security.CustomUserDetails;
import com.izikgram.user.entity.User;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/board")
public class BoardRestController {

    @Autowired
    private BoardService boardService;

    // 1. 화면에서 필요한 api가 뭔지
    // 2. api에서 필요한 기능이 뭔지
    // 3. 필요한 기능에서 필요한 쿼리가 뭔지

    // 좋아요/싫어요 토글
    @PostMapping("/{boardId}/{boardType}/update")
    public Map<String, Integer> updateLikeCount(@PathVariable("boardId") int boardId,
                                                @PathVariable("boardType") int boardType,
                                                @RequestParam boolean isLiked, @AuthenticationPrincipal CustomUserDetails userDetails) {
        log.info("요청 게시글 번호: {}", boardId);
        log.info("요청 종류: {}", isLiked);

        //유저 객체 처리해야함...
        User user = userDetails.getUser();
        String memberId = user.getMember_id();



        // isLiked(true: 좋아요 요청, false: 싫어요 요청)
        return boardService.updateLikeCount(boardId, memberId, isLiked,boardType);
    }

}
