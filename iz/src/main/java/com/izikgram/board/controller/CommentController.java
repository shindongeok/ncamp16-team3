package com.izikgram.board.controller;


import com.izikgram.board.service.BoardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/board/")
public class CommentController {

    @Autowired
    private BoardService boardService;



}