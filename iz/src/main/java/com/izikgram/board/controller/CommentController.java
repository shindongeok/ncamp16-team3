package com.izikgram.board.controller;


import com.izikgram.board.entity.CommentDto;
import com.izikgram.board.service.BoardService;
import com.izikgram.global.security.CustomUserDetails;
import com.izikgram.user.entity.User;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/board/")
public class CommentController {

    @Autowired
    private BoardService boardService;


    @PostMapping("/{board_id}/comment")
    public ResponseEntity<Map<String, Object>> writeComment(
            @PathVariable("board_id") int boardId,
            @RequestParam("comment_content") String commentContent,
            @RequestParam("board_type") int boardType,
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        Map<String, Object> response = new HashMap<>();

        try {
            // 로그인한 사용자 정보 가져오기
            User user = userDetails.getUser();

            String memberId = user.getMember_id();
            String nickname = user.getNickname();

            // 댓글 저장 (board_type에 따라 다른 테이블에 저장)
            if (boardType == 1) {
                boardService.addComment01(boardId, memberId, commentContent);
            } else if (boardType == 2) {
                boardService.addComment02(boardId, memberId, commentContent);
            }

            // 새로 추가된 댓글을 조회
            CommentDto newComment = boardService.getLastComment(boardId, boardType); // 새 댓글 조회 메소드 추가

            // 응답 데이터 설정
            response.put("nickname", newComment.getNickname());
            response.put("comment_content", newComment.getComment_content());
            response.put("reg_date", newComment.getReg_date().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
            response.put("success", true);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("댓글 작성 중 오류 발생: {}", e.getMessage(), e);
            response.put("success", false);
            response.put("error", "댓글 저장에 실패했습니다.");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }


}