package com.izikgram.board.service;

import com.izikgram.alarm.service.AlarmService;
import com.izikgram.alarm.service.SseEmitterService;
import com.izikgram.board.entity.Board;

import com.izikgram.board.entity.BoardDto;
import com.izikgram.board.entity.CommentDto;
import com.izikgram.board.repository.BoardMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class BoardService {

    @Autowired
    private BoardMapper boardMapper;

    @Autowired
    private SseEmitterService sseEmitterService;

    @Autowired
    private AlarmService alarmService;

    //게시판 종류 조회
    public String findBoardName(int board_type){

        String boardName = boardMapper.getBoardName(board_type);

        return boardName;
    }

    // 게시판 리스트 조회
    public List<BoardDto> SelectBoardList01(int board_type) {
        return boardMapper.getBoard01(board_type);
    }

    public List<BoardDto> SelectBoardList02(int board_type) {
        return boardMapper.getBoard02(board_type);
    }

    // 댓글리스트 조회
    public List<CommentDto> selectComment01(int board_id ){
        return boardMapper.commentGetList01(board_id);
    }

    public List<CommentDto> selectComment02(int board_id){
        return boardMapper.commentGetList02(board_id);
    }

    //댁슬 카운트 조회

    // 작성글 삽입
    public void insertPost01(int board_type, String writer_id, String title, String content) {
        boardMapper.insertBoard01(writer_id,title, content, board_type);
    }

    public void insertPost02(int board_type, String writer_id, String title, String content) {
        boardMapper.insertBoard02(writer_id,title, content, board_type);
    }

    //게시글 상세보기
    public Board selectDetail01(int board_id){
        return boardMapper.selectBoard01(board_id);
    }

    public Board selectDetail02(int board_id){
        return boardMapper.selectBoard02(board_id);
    }

    //자유/하소연 게시글 업데이트
    public void modifyBoard01(int board_id, String title, String content){
        boardMapper.updateBoard01(board_id, title, content);
    }
    public void modifyBoard02(int board_id, String title, String content){
        boardMapper.updateBoard02(board_id, title, content);
    }

    // 좋아요 구현
    public Map<String, Integer> updateLikeCount(int boardId, String memberId, boolean isLiked, int boardType) {
        // 사용자가 게시글에 좋아요/싫어요한 개수

        if(boardType == 1) {
            int like = boardMapper.countLikeByMember(boardId, memberId);
            int dislike = boardMapper.countDislikeByMember(boardId, memberId);

            log.info("like: {}, dislike: {}", like, dislike);

            if (isLiked) {
                // 좋아요를 요청한 경우
                increaseLike(boardId, memberId, like, dislike);
            } else {
                // 싫어요를 요청한 경우
                increaseDisLike(boardId, memberId, like, dislike);
            }

            // 요청끝나고 게시글의 좋아요/싫어요 총 개수
            return getStatusResult(boardId);
        }else if (boardType == 2) {
            int like = boardMapper.countLikeByMember02(boardId, memberId);
            int dislike = boardMapper.countDislikeByMember02(boardId, memberId);

            log.info("like: {}, dislike: {}", like, dislike);

            if (isLiked) {
                // 좋아요를 요청한 경우
                increaseLike02(boardId, memberId, like, dislike);
            } else {
                // 싫어요를 요청한 경우
                increaseDisLike02(boardId, memberId, like, dislike);
            }

            // 요청끝나고 게시글의 좋아요/싫어요 총 개수
            return getStatusResult02(boardId);
        }
        return null;
    }

    // 좋아요 증가
    private void increaseLike(int boardId, String memberId, int like, int dislike) {
        log.info("좋아요 증가");

        if (like == 0 && dislike == 0) {
            // 선택한 적이 없는 경우
            boardMapper.insertLike(boardId, memberId);
            boardMapper.upLikeCount(boardId);
        } else if (dislike > 0 && like == 0) {
            // 싫어요 상태인 경우
            boardMapper.deleteDislike(boardId, memberId);
            boardMapper.insertLike(boardId, memberId);
            boardMapper.downDislikeCount(boardId);
            boardMapper.upLikeCount(boardId);
        } else {
            // 좋아요 상태인 경우
            boardMapper.deleteLike(boardId, memberId);
            boardMapper.downLikeCount(boardId);
        }
    }

    // 모든 업데이트 이후 게시글의 좋아요/싫어요 개수 반환
    private Map<String, Integer> getStatusResult(int boardId) {

        Board board1 = boardMapper.totalLikeDisLike(boardId);
        int like = board1.getLike_count();
        int dislike = board1.getDislike_count();

        Map<String, Integer> map = new HashMap<>();
        map.put("like", like);
        map.put("dislike", dislike);

        return map;
    }

    // 싫어요 증가
    private void increaseDisLike(int boardId, String memberId, int like, int dislike) {
        log.info("싫어요 증가");

        if (like == 0 && dislike == 0) {
            // 선택한적이 없는 경우
            boardMapper.insertDislike(boardId, memberId);
            boardMapper.upDislikeCount(boardId);
        } else if (like > 0 && dislike == 0) {
            // 좋아요 상태인 경우
            boardMapper.deleteLike(boardId, memberId);
            boardMapper.insertDislike(boardId, memberId);
            boardMapper.downLikeCount(boardId);
            boardMapper.upDislikeCount(boardId);
        } else {
            // 싫어요 상태인 경우
            boardMapper.deleteDislike(boardId, memberId);
            boardMapper.downDislikeCount(boardId);
        }
    }


    // 좋아요 증가
    private void increaseLike02(int boardId, String memberId, int like, int dislike) {
        log.info("좋아요 증가");

        if (like == 0 && dislike == 0) {
            // 선택한 적이 없는 경우
            boardMapper.insertLike02(boardId, memberId);
            boardMapper.upLikeCount02(boardId);
        } else if (dislike > 0 && like == 0) {
            // 싫어요 상태인 경우
            boardMapper.deleteDislike02(boardId, memberId);
            boardMapper.insertLike02(boardId, memberId);
            boardMapper.downDislikeCount02(boardId);
            boardMapper.upLikeCount02(boardId);
        } else {
            // 좋아요 상태인 경우
            boardMapper.deleteLike02(boardId, memberId);
            boardMapper.downLikeCount02(boardId);
        }
    }

    // 싫어요 증가
    private void increaseDisLike02(int boardId, String memberId, int like, int dislike) {
        log.info("싫어요 증가");

        if (like == 0 && dislike == 0) {
            // 선택한적이 없는 경우
            boardMapper.insertDislike02(boardId, memberId);
            boardMapper.upDislikeCount02(boardId);
        } else if (like > 0 && dislike == 0) {
            // 좋아요 상태인 경우
            boardMapper.deleteLike02(boardId, memberId);
            boardMapper.insertDislike02(boardId, memberId);
            boardMapper.downLikeCount02(boardId);
            boardMapper.upDislikeCount02(boardId);
        } else {
            // 싫어요 상태인 경우
            boardMapper.deleteDislike02(boardId, memberId);
            boardMapper.downDislikeCount02(boardId);
        }
    }

    // 모든 업데이트 이후 게시글의 좋아요/싫어요 개수 반환
    private Map<String, Integer> getStatusResult02(int boardId) {

        Board board1 = boardMapper.totalLikeDisLike02(boardId);
        int like = board1.getLike_count();
        int dislike = board1.getDislike_count();

        Map<String, Integer> map = new HashMap<>();
        map.put("like", like);
        map.put("dislike", dislike);

        return map;
    }

    //댓글 저장
    public void addComment01(int board_id, String writer_id, String comment){
        boardMapper.addComment01(board_id, writer_id, comment);
        Board board = boardMapper.selectBoard01(board_id);
        String content = "[" + board.getTitle()  + "] \n게시글에 댓글이 달렸습니다.";
        sseEmitterService.send(board.getWriter_id(), content);

        log.info(board.toString());
        alarmService.save(board.getWriter_id(), board.getBoard_type(), board.getBoard_id(), content);
    }
    public void addComment02(int board_id, String writer_id, String comment){
        boardMapper.addComment02(board_id, writer_id, comment);
        Board board = boardMapper.selectBoard02(board_id);
        String content = "[" + board.getTitle()  + "] \n게시글에 댓글이 달렸습니다.";
        sseEmitterService.send(board.getWriter_id(), content);
        alarmService.save(board.getWriter_id(), board.getBoard_type(), board.getBoard_id(), content);
    }

    //댓글작성한거 반환
    public CommentDto getLastComment(int boardId, int boardType) {
        if (boardType == 1) {
            return boardMapper.getLastComment01(boardId);
        } else if (boardType == 2) {
            return boardMapper.getLastComment02(boardId);
        }
        return null;
    }

    //-------------------------------
    // 댓글 삭제 서비스
    // 게시판 1 댓글 삭제 메서드
    public boolean deleteComment01(int commentId, int boardId) {
        try {
            // MyBatis를 사용하여 댓글을 삭제하는 쿼리 호출
            log.info("쿼리 실행 전: commentId = {}, boardId = {}", commentId, boardId);
            boardMapper.deleteComment01(boardId, commentId);
            return true; // 삭제가 성공적으로 완료되면 true 반환
        } catch (Exception e) {
            e.printStackTrace();
            return false; // 실패 시 false 반환
        }
    }

    // 게시판 2 댓글 삭제 메서드
    public boolean deleteComment02(int commentId, int boardId) {
        try {
            // MyBatis를 사용하여 댓글을 삭제하는 쿼리 호출
            boardMapper.deleteComment02(boardId, commentId);
            return true; // 삭제가 성공적으로 완료되면 true 반환
        } catch (Exception e) {
            e.printStackTrace();
            return false; // 실패 시 false 반환
        }
    }

    //인기게시판
    public Map<String, List<BoardDto>> getIssueBoardList01(){
        Map<String, List<BoardDto>> map = new HashMap<>();
        List<BoardDto> issueBoardList01 = boardMapper.getIssueBoardList01();
        List<BoardDto> issueBoardList02 = boardMapper.getIssueBoardList02();


        log.info("issueBoardList01:{}, issueBoardList02 {} ", issueBoardList01, issueBoardList02);
        map.put("issueBoardList01", issueBoardList01);
        map.put("issueBoardList02", issueBoardList02);

        return map;
    }

    public Map<String, List<BoardDto>> getMyBoardList(String writer_id){
        Map<String, List<BoardDto>> map = new HashMap<>();
        List<BoardDto> myBoardList01 = boardMapper.getMyBoardList01(writer_id);
        List<BoardDto> myBoardList02 = boardMapper.getMyBoardList02(writer_id);

        myBoardList01 = (myBoardList01.size() > 5) ? myBoardList01.subList(0, 5) : myBoardList01;
        myBoardList02 = (myBoardList02.size() > 5) ? myBoardList02.subList(0, 5) : myBoardList02;


        log.info("myBoardList01:{}, myBoardList02 {} ", myBoardList01, myBoardList02);
        map.put("myBoardList01", myBoardList01);
        map.put("myBoardList02", myBoardList02);

        return map;

    }
}
