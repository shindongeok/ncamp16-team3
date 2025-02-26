package com.izikgram.board.controller;

import com.izikgram.board.entity.Board;
import com.izikgram.board.entity.BoardDto;
import com.izikgram.board.entity.CommentDto;
import com.izikgram.board.service.BoardService;
import com.izikgram.global.security.CustomUserDetails;
import com.izikgram.user.entity.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@Slf4j
@Controller
@RequestMapping("/board")
public class  BoardController {


    @Autowired
    private BoardService boardService;


    //자유,하소연 게시판 리스트
    @GetMapping("/{board_type}")
    public String boardCommunity(@PathVariable("board_type") int board_type, Model model){

        if (board_type != 1 && board_type != 2) {
            model.addAttribute("error", "유효하지 않은 게시판 타입입니다.");
            return "redirect:/main";
        }

        String board_name = boardService.findBoardName(board_type);

        List<BoardDto> listBoard;
        if (board_type == 1) {
            listBoard = boardService.SelectBoardList01(board_type);
        } else {
            listBoard = boardService.SelectBoardList02(board_type);
        }

        model.addAttribute("board_name", board_name);
        model.addAttribute("listBoard", listBoard);


        return "board/community";
    }

    //자유,하소연 작성하기 페이지 이동
    @GetMapping("/postForm")
    public String postForm(@RequestParam("board_type")int board_type,
                           Model model){
        log.info("boardType : {}",board_type);
        if (board_type != 1 && board_type != 2) {
            model.addAttribute("error", "유효하지 않은 게시판 타입입니다.");
            return "redirect:/board/" + board_type;
        }

        String board_name = boardService.findBoardName(board_type);

        model.addAttribute("board_name", board_name);
        model.addAttribute("board_type", board_type);

        return "board/postForm";
    }

    // 글작성
    @PostMapping("/{board_type}/post")
    public String insertPost(@PathVariable("board_type") int board_type,
                             @AuthenticationPrincipal CustomUserDetails userDetails,
                             @RequestParam("title") String title,
                             @RequestParam("content") String content) {

        if (board_type != 1 && board_type != 2) {
            return "redirect:/board/" + board_type;
        }

        User user = userDetails.getUser();
        String writer_id = user.getMember_id();

        if (board_type == 1) {
            boardService.insertPost01(board_type, writer_id, title, content);
        }else if (board_type == 2) {
            boardService.insertPost02(board_type, writer_id, title, content);
        }

        return "redirect:/board/" + board_type;
    }

    //자유,하소연 상세보기
//    @GetMapping("/{board_type}/{board_id}")
//    public String postDetail(@PathVariable("board_type") int board_type,
//                             @PathVariable("board_id") int board_id,
//                             @AuthenticationPrincipal CustomUserDetails userDetails,
//                             Model model ){
//
//        User user = userDetails.getUser();
//        String writer_id = user.getMember_id();
//
//        String board_name = boardService.findBoardName(board_type);
//
//        Board board = new Board();
//        List<CommentDto> ListCommentDto;
//        if(board_type == 1){
//            ListCommentDto = boardService.selectComment01(board_id);
//            board = boardService.selectDetail01(board_id);
//        }else if(board_type == 2){
//            ListCommentDto = boardService.selectComment02(board_id);
//            board = boardService.selectDetail02(board_id);
//        }else {
//            model.addAttribute("error", "유효하지 않은 게시판 타입입니다.");
//            return "redirect:/board/" + board_type;
//        }
//
//        model.addAttribute("board_name", board_name);
//        model.addAttribute("board", board);
//        model.addAttribute("member_id", writer_id);
//        model.addAttribute("comment", ListCommentDto);
//
//        return "board/postDetail";
//    }
    // 자유, 하소연 상세보기
    @GetMapping("/{board_type}/{board_id}")
    public String postDetail(@PathVariable("board_type") int board_type,
                             @PathVariable("board_id") int board_id,
                             @AuthenticationPrincipal CustomUserDetails userDetails,
                             Model model) {


        User user = userDetails.getUser();
        String writer_id = user.getMember_id();

        String board_name = boardService.findBoardName(board_type);

        List<CommentDto> commentList;
        Board board = null;

        if (board_type == 1) {
            commentList = boardService.selectComment01(board_id);
            board = boardService.selectDetail01(board_id);
        } else if (board_type == 2) {
            commentList = boardService.selectComment02(board_id);
            board = boardService.selectDetail02(board_id);
        } else {
            model.addAttribute("error", "유효하지 않은 게시판 타입입니다.");
            return "redirect:/board/" + board_type;
        }

        // 모델에 데이터 추가
        model.addAttribute("board_name", board_name);
        model.addAttribute("board", board);
        model.addAttribute("member_id", writer_id);
        model.addAttribute("comment", commentList);

        return "board/postDetail";
    }


    //자유,하소연 게시글 수정하기페이지 이동
    @GetMapping("/update/{board_type}/{board_id}")
    public String updatePost(@PathVariable("board_type") int board_type,
                             @PathVariable("board_id") int board_id, Model model){
        Board board = new Board();
        if(board_type == 1){
            board = boardService.selectDetail01(board_id);
        }if(board_type == 2){
            board = boardService.selectDetail02(board_id);
        }

        model.addAttribute("board", board);

        return "board/postFormModify";
    }

    //자유, 하소연 게시글 수정하기
    @PostMapping("/{board_type}/{board_id}")
    public String modifyPost(@PathVariable("board_type") int board_type,
                             @PathVariable("board_id") int board_id,
                             @RequestParam("title") String title,
                             @RequestParam("content") String content,
                             @AuthenticationPrincipal CustomUserDetails userDetails) {

        User user = userDetails.getUser();
        String member_id = user.getMember_id();

        if (board_type == 1) {
            Board board = boardService.selectDetail01(board_id);
            String writer_id = board.getWriter_id();

            if (member_id != null && member_id.equals(writer_id)) {
                boardService.modifyBoard01(board_id, title, content);
                return "redirect:/board/" + board_type;
            }

        } else if (board_type == 2) {
            Board board = boardService.selectDetail02(board_id);
            String writer_id = board.getWriter_id();

            if (member_id != null && member_id.equals(writer_id)) {
                boardService.modifyBoard02(board_id, title, content);
                return "redirect:/board/" + board_type;
            }
        }
        return "redirect:/board/" + board_type;
    }

    //인기게시판
    @GetMapping("/hot")
    public String hotCommunity(Model model){

        Map<String, List<BoardDto>> issueBoardList = boardService.getIssueBoardList01();
        List<BoardDto> issueBoardList01 = issueBoardList.get("issueBoardList01");
        List<BoardDto> issueBoardList02 = issueBoardList.get("issueBoardList02");

        log.info("issueBoardList01: {}", issueBoardList01);
        log.info("issueBoardList02: {}", issueBoardList02);
        model.addAttribute("issueBoardList01",issueBoardList01);
        model.addAttribute("issueBoardList02",issueBoardList02);

        return "board/popularityCommunity";
    }

    // 내가 작성한 게시글 리스트
    @GetMapping("/myboard")
    public String myBoard( @AuthenticationPrincipal CustomUserDetails userDetails,
                           Model model){

        User user = userDetails.getUser();
        String writer_id = user.getMember_id();

        Map<String, List<BoardDto>> MyBoardList = boardService.getMyBoardList(writer_id);
        List<BoardDto> myBoardList01 = MyBoardList.get("myBoardList01");
        List<BoardDto> myBoardList02 = MyBoardList.get("myBoardList02");

        model.addAttribute("myBoardList01",myBoardList01);
        model.addAttribute("myBoardList02",myBoardList02);

        return "board/myBoard";
    }

    // 내가 작성한 자유게시글 리스트
    @GetMapping("/myBoard/feelMyBoard")
    public String feelMyBoard(@AuthenticationPrincipal CustomUserDetails userDetails,
                              Model model){

        User user = userDetails.getUser();
        String writer_id = user.getMember_id();

        Map<String, List<BoardDto>> MyBoardList = boardService.getMyBoardList02(writer_id);
        List<BoardDto> myBoardList01 = MyBoardList.get("myBoardList01");

        model.addAttribute("myBoardList01",myBoardList01);

        return "board/feelMyBoard";
    }

    // 내가 작성한 하소연게시글 리스트
    @GetMapping("/myBoard/whiningMyBoard")
    public String whiningMyBoard(@AuthenticationPrincipal CustomUserDetails userDetails,
                                 Model model){

        User user = userDetails.getUser();
        String writer_id = user.getMember_id();

        Map<String, List<BoardDto>> MyBoardList = boardService.getMyBoardList02(writer_id);
        List<BoardDto> myBoardList02 = MyBoardList.get("myBoardList02");

        model.addAttribute("myBoardList02",myBoardList02);

        return "board/whiningMyBoard";
    }

    // 게시글 삭제
    @PostMapping("/delete/{board_type}/{board_id}")
    public String boardDelete(@PathVariable("board_id") int board_id,
                              @PathVariable("board_type")int board_type){

        if(board_type == 1){
            boardService.deleteBoard01(board_id);
        }else if(board_type == 2){
            boardService.deleteBoard02(board_id);
        }
        return "redirect:/board/" + board_type;
    }

}




