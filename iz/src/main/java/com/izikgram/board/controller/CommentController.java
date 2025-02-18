package com.izikgram.board.controller;

import com.izikgram.board.entity.Board;
import com.izikgram.board.service.BoardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/board/")
public class CommentController {

    @Autowired
    private BoardService boardService;

//    @PostMapping("/comment")
//    public ResponseEntity<?> writeComment(@PathVariable("boardIdx") Integer boardIdx) {
//        //...
//        // Comment comment = commentService.newComment();
//        //return ResponseEntity.ok().body(comment);
//        return ResponseEntity.ok().body("게시글 성공");
//    }

    @PostMapping("/new")
    public void boardInsert(Board vo){

    }
}