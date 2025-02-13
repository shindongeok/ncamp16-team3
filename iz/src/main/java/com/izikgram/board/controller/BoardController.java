package com.izikgram.board.controller;

import com.izikgram.board.entity.Board;
import com.izikgram.board.service.BoardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/board")
public class BoardController {

    @Autowired
    private BoardService boardService;

    //자유,하소연 게시판
    @GetMapping("/{board_type}")
    public String boardCommunity(@PathVariable("board_type") int board_type, Model model){

        if (board_type != 1 && board_type != 2) {
            model.addAttribute("error", "유효하지 않은 게시판 타입입니다.");
            return "redirect:/";
        }

        String board_name = boardService.findBoardName(board_type);
        List<Board> listBoard  = boardService.findBoardList(board_type);
        System.out.println("list :" + listBoard);
        System.out.println(board_type);
        model.addAttribute("board_name", board_name);
        model.addAttribute("listBoard", listBoard);
        model.addAttribute("board_type", board_type);

        return "/board/community";
    }


    //자유,하소연 작성하기 페이지 이동
    @GetMapping("/postForm")
    public String postForm(@RequestParam("board_type") int board_type, Model model){

        if (board_type != 1 && board_type != 2) {
            model.addAttribute("error", "유효하지 않은 게시판 타입입니다.");
            return "redirect:/";
        }
        System.out.println(board_type);

        String board_name = boardService.findBoardName(board_type);


        model.addAttribute("board_name", board_name);
        model.addAttribute("board_type", board_type);


        return "/board/postForm";
    }

    // 글작성
    @PostMapping("/{board_type}/post")
    public String insertPost(
            @PathVariable("board_type") int board_type,
            @RequestParam("writer_id") String writer_id,
            @RequestParam("title") String title,
            @RequestParam("content") String content) {

        // board_type 유효성 검사
        if (board_type != 1 && board_type != 2) {
            return "/index";
        }

        // 게시글 삽입
        boardService.insertPost(board_type, writer_id, title, content);
        System.out.println(board_type+ "," + writer_id + "," + title + "," + content);

        return "redirect:/board/" + board_type;
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

}
