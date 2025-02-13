package com.izikgram.board.controller;

import com.izikgram.board.entity.Board;
import com.izikgram.board.service.BoardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/board")
public class BoardController {

    @Autowired
    private BoardService boardService;

    //자유,하소연 게시판
    @GetMapping("/{board_type}")
    public String boardCommunity(@PathVariable("board_type") int boardType, Model model){

        if (boardType != 1 && boardType != 2) {
            model.addAttribute("error", "유효하지 않은 게시판 타입입니다.");
            return "redirect:/";
        }

        String boardName = boardService.findBoardName(boardType);
        List<Board> listBoard  = boardService.findBoardList(boardType);
        System.out.println("list :" + listBoard);
        System.out.println(boardType);
        model.addAttribute("boardName", boardName);
        model.addAttribute("listBoard", listBoard);
        model.addAttribute("boardType", boardType);

        return "/board/community";
    }

    //인기게시판
    @GetMapping("/hot")
    public String hotCommunity(){
        return "/board/popularityCommunity";
    }

    //자유,하소연 상세보기
    @GetMapping("/{board_type}/{board_id}")
    public String postDetail(){
        return "/board/postDetail";
    }

    //자유,하소연 수정
    @GetMapping("update")
    public String updatePost(){
        return "/board/postForm";
    }
}
