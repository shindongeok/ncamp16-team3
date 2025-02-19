//package com.izikgram.board.service;
//
//import com.izikgram.alarm.service.AlarmService;
//import com.izikgram.board.repository.BoardMapper;
//import com.izikgram.board.repository.CommentMapper;
//import lombok.RequiredArgsConstructor;
//import org.springframework.stereotype.Service;
//
//@Service
//@RequiredArgsConstructor
//public class CommentService {
//
//    private final CommentMapper commentMapper;
//    private final AlarmService alarmService;
//
//    // 댓글 추가 메서드
//    public void addComment(int boardId, String commentContent, String writerId) {
//        // 1. 댓글을 DB에 삽입
//        commentMapper.addComment(boardId, writerId, commentContent);
//
//        // 2. 댓글이 삽입된 후 게시글 작성자에게 알림을 보냄
//        alarmService.AlarmComment(boardId);
//    }
//}
