package com.izikgram.board.controller;


import com.izikgram.board.entity.BoardDto;
import com.izikgram.board.service.BoardService;
import com.izikgram.global.security.CustomUserDetails;
import com.izikgram.user.entity.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/board")
public class BoardRestController {

    @Autowired
    private BoardService boardService;

    // 좋아요/싫어요 토글
    @PostMapping("/{boardId}/{boardType}/update")
    public Map<String, Integer> updateLikeCount(@PathVariable("boardId") int boardId,
                                                @PathVariable("boardType") int boardType,
                                                @RequestParam boolean isLiked,
                                                @AuthenticationPrincipal CustomUserDetails userDetails) {

        log.info("요청 게시글 번호: {}", boardId);
        log.info("요청 종류: {}", isLiked);

        User user = userDetails.getUser();
        String memberId = user.getMember_id();

        return boardService.updateLikeCount(boardId, memberId, isLiked,boardType);
    }

    //게시판 리스트 셀렉트옵션
    @PostMapping("/{board_type}")
    public ResponseEntity<Map<String, Object>> getBoardList(
            @PathVariable("board_type") int boardType,
            @RequestParam(defaultValue = "newest") String sort) {

        List<BoardDto> boardList = boardService.getBoardList(boardType, sort);

        Map<String, Object> response = new HashMap<>();
        response.put("boardList", boardList);

        return ResponseEntity.ok(response);
    }

}
