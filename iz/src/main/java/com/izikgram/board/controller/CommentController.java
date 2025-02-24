package com.izikgram.board.controller;


import com.izikgram.board.entity.CommentDto;
import com.izikgram.board.service.BoardService;
import com.izikgram.global.security.CustomUserDetails;
import com.izikgram.user.entity.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

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
            response.put("comment_id", newComment.getComment_id());
            response.put("writer_id", newComment.getWriter_id());
            response.put("success", true);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("댓글 작성 중 오류 발생: {}", e.getMessage(), e);
            response.put("success", false);
            response.put("error", "댓글 저장에 실패했습니다.");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    //---------------------------------------
    // 댓글 삭제
    @DeleteMapping("/deleteComment")
    public ResponseEntity<Map<String, String>> deleteComment(
            @RequestParam("commentId") int commentId,
            @RequestParam("boardId") int boardId,
            @RequestParam("boardType") int boardType,
            @RequestParam("writerId") String writerId,
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        Map<String, String> response = new HashMap<>();

        try {
            String memberId = userDetails.getUser().getMember_id();
            boolean isDeleted = false;

            log.info("commentId : {}", commentId);
            log.info("boardId : {}", boardId);
            log.info("boardType : {}", boardType);
            log.info("writerId : {}", writerId);


            // 현재 로그인한 사용자와 댓글 작성자가 동일한 경우에만 삭제 처리
            if (memberId != null && memberId.equals(writerId)) {
                if (boardType == 1) {
                    isDeleted = boardService.deleteComment01(commentId, boardId);
                } else if (boardType == 2) {
                    isDeleted = boardService.deleteComment02(commentId, boardId);
                }
            }

            // 삭제 여부에 따라 응답 처리
            if (isDeleted) {
                log.info("댓글 삭제 성공: commentId = {}, boardId = {}", commentId, boardId);
                response.put("message", "댓글이 삭제되었습니다.");
                return ResponseEntity.ok(response);
            } else {
                response.put("message", "댓글 삭제에 실패했습니다.");
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
            }

        } catch (Exception e) {
            // 오류 처리
            response.put("message", "댓글 삭제 중 오류가 발생했습니다.");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

}