package com.izikgram.board.service;

import com.izikgram.alarm.entity.AlarmType;
import com.izikgram.alarm.service.AlarmService;
import com.izikgram.alarm.service.SseEmitterService;
import com.izikgram.board.entity.Board;

import com.izikgram.board.entity.BoardDto;
import com.izikgram.board.entity.Comment;
import com.izikgram.board.entity.CommentDto;
import com.izikgram.board.repository.BoardMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Slf4j
@Service
public class BoardService {

    @Autowired
    private BoardMapper boardMapper;

    @Autowired
    private SseEmitterService sseEmitterService;

    @Autowired
    private AlarmService alarmService;


    //게시판 이름 조회
    public String findBoardName(int board_type){

        return boardMapper.getBoardName(board_type);
    }

    // 게시판 리스트조회
    public List<BoardDto> getBoardList(int boardType, String sort) {

        return boardMapper.getBoardList(boardType, sort);
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

            checkPopularBoardsAndSendAlarm();

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

            checkPopularBoardsAndSendAlarm();

            // 요청끝나고 게시글의 좋아요/싫어요 총 개수
            return getStatusResult02(boardId);
        }

        return null;
    }

    // 좋아요 눌렀을 때, 인기게시글 리스트 확인 후 알람 전송
    private void checkPopularBoardsAndSendAlarm() {

        // Top3 리스트 반환
        List<BoardDto> issueBoardList01 = boardMapper.getPopularBoardListBoard1();
        List<BoardDto> issueBoardList02 = boardMapper.getPopularBoardListBoard2();

        log.info("issueBoardList01 : {}", issueBoardList01);
        log.info("issueBoardList02 : {}", issueBoardList02);

        for (BoardDto board : issueBoardList01) {
            // 읽음 여부 상관없이 인기게시글이면 (!true 값 반환) => 알림 안보냄
            if (!alarmService.hasPopularAlarm(board.getBoard_id())) {
                String content = "[" + board.getTitle() + "] \n 인기게시글이 되었습니다!!";
                sseEmitterService.popularSend(board.getWriter_id(), content);
                alarmService.save(board.getWriter_id(), board.getBoard_type(), board.getBoard_id(), content, AlarmType.POPULAR);
            }
        }

        for (BoardDto board : issueBoardList02) {
            if (!alarmService.hasPopularAlarm(board.getBoard_id())) {
                String content = "[" + board.getTitle() + "] \n 인기게시글이 되었습니다!!";
                sseEmitterService.popularSend(board.getWriter_id(), content);
                alarmService.save(board.getWriter_id(), board.getBoard_type(), board.getBoard_id(), content, AlarmType.POPULAR);
            }
        }
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

        checkAndInsertPopularBoard(boardId);
    }

    // 좋아요를 눌렀을때 인기 테이블 반영
    private void checkAndInsertPopularBoard(int boardId) {
        int likeCount = boardMapper.getLikeCount(boardId); // 현재 좋아요 개수 조회
        boolean isPopular = boardMapper.isPopularBoard(boardId, 1); // 이미 인기 게시글인지 확인

        if (likeCount >= 5) {
            if (!isPopular) { // 등록되지 않았다면 추가
                boardMapper.insertPopularBoard(boardId, 1);
                log.info("01게시글 {}이(가) 인기 게시판에 등록되었습니다!", boardId);
            } else { // 이미 등록된 경우 업데이트
                boardMapper.updatePopularBoard(boardId, 1, likeCount);
                log.info("01게시글 {}의 인기 게시판 정보가 업데이트되었습니다!", boardId);
            }
        } else if (likeCount <= 4) { // 좋아요 수가 4개 이하이면 인기 게시판에서 삭제
            boardMapper.deletePopularBoard(boardId, 1);
            log.info("01게시글 {}이(가) 인기 게시판에서 삭제되었습니다!", boardId);
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

        // 인기 게시판 상태 업데이트
        updatePopularBoardOnLikeDislikeChange01(boardId);
    }

    //싫어요 눌렀을 때 인기테이블 반영
    private void updatePopularBoardOnLikeDislikeChange01(int boardId) {
        int likeCount = boardMapper.getLikeCount(boardId); // 좋아요 개수 조회
        boolean isPopular = boardMapper.isPopularBoard(boardId, 1); // 인기 게시판에 있는지 확인

        if (likeCount >= 5 && isPopular) {
            // 좋아요가 5개 이상일 때 인기 게시판에 등록
            boardMapper.updatePopularBoard(boardId, 1,likeCount);
            log.info("게시글 {}이(가) 인기 게시판에 등록되었습니다!", boardId);
        } else if (likeCount < 5 && isPopular) {
            // 좋아요가 5개 미만일 때 인기 게시판에서 삭제
            boardMapper.deletePopularBoard(boardId, 1);
            log.info("게시글 {}이(가) 인기 게시판에서 삭제되었습니다!", boardId);
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

        checkAndInsertPopularBoard02(boardId);
    }

    private void checkAndInsertPopularBoard02(int boardId) {
        int likeCount = boardMapper.getLikeCount02(boardId);
        boolean isPopular = boardMapper.isPopularBoard(boardId, 2);

        if (likeCount >= 5) {
            if (!isPopular) { // 등록되지 않았다면 추가
                boardMapper.insertPopularBoard(boardId, 2);
                log.info("01게시글 {}이(가) 인기 게시판에 등록되었습니다!", boardId);
            } else { // 이미 등록된 경우 업데이트
                boardMapper.updatePopularBoard(boardId, 2, likeCount);
                log.info("02게시글 {}의 인기 게시판 정보가 업데이트되었습니다!", boardId);
            }
        } else if (likeCount <= 4 ) { // 좋아요 수가 4개 이하이면 인기 게시판에서 삭제
            boardMapper.deletePopularBoard(boardId, 2);
            log.info("02게시글 {}이(가) 인기 게시판에서 삭제되었습니다!", boardId);
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

        updatePopularBoardOnLikeDislikeChange02(boardId);
    }

    //싫어요 눌렀을 때 인기테이블 반영
    private void updatePopularBoardOnLikeDislikeChange02(int boardId) {
        int likeCount = boardMapper.getLikeCount02(boardId); // 좋아요 개수 조회
        boolean isPopular = boardMapper.isPopularBoard(boardId, 2); // 인기 게시판에 있는지 확인

        if (likeCount >= 5 && isPopular) {
            // 좋아요가 5개 이상일 때 인기게시판 업데이트
            boardMapper.updatePopularBoard(boardId, 2,likeCount);
            log.info("게시글 {}이(가) 인기 게시판에 등록되었습니다!", boardId);
        } else if (likeCount < 5 && isPopular) {
            // 좋아요가 5개 미만일 때 인기 게시판에서 삭제
            boardMapper.deletePopularBoard(boardId, 2);
            log.info("게시글 {}이(가) 인기 게시판에서 삭제되었습니다!", boardId);
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
        alarmService.save(board.getWriter_id(), board.getBoard_type(), board.getBoard_id(), content, AlarmType.valueOf("COMMENT"));
        log.info(board.toString());
    }
    public void addComment02(int board_id, String writer_id, String comment){
        boardMapper.addComment02(board_id, writer_id, comment);
        Board board = boardMapper.selectBoard02(board_id);
        String content = "[" + board.getTitle()  + "] \n게시글에 댓글이 달렸습니다.";
        sseEmitterService.send(board.getWriter_id(), content);
        alarmService.save(board.getWriter_id(), board.getBoard_type(), board.getBoard_id(), content, AlarmType.valueOf("COMMENT"));
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

    // 댓글 삭제
    // 게시판 1 댓글
    public boolean deleteComment01(int commentId, int boardId) {
        try {
            log.info("쿼리 실행 전: commentId = {}, boardId = {}", commentId, boardId);
            boardMapper.deleteComment01(boardId, commentId);
            return true; // 삭제가 성공적으로 완료되면 true 반환
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // 게시판 2 댓글
    public boolean deleteComment02(int commentId, int boardId) {
        try {
            boardMapper.deleteComment02(boardId, commentId);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    //인기게시판
    public Map<String, List<BoardDto>> getIssueBoardList01(){
        Map<String, List<BoardDto>> map = new HashMap<>();
        List<BoardDto> issueBoardList01 = boardMapper.getPopularBoardListBoard1();
        List<BoardDto> issueBoardList02 = boardMapper.getPopularBoardListBoard2();

        log.info("issueBoardList01:{}, issueBoardList02 {} ", issueBoardList01, issueBoardList02);
        map.put("issueBoardList01", issueBoardList01);
        map.put("issueBoardList02", issueBoardList02);

        return map;
    }




    //내가 작성한 게시글 5개 보여주기
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

    public Map<String, List<BoardDto>> getMyBoardList02(String writer_id){
        Map<String, List<BoardDto>> map = new HashMap<>();
        List<BoardDto> myBoardList01 = boardMapper.getMyBoardList01(writer_id);
        List<BoardDto> myBoardList02 = boardMapper.getMyBoardList02(writer_id);

        map.put("myBoardList01", myBoardList01);
        map.put("myBoardList02", myBoardList02);

        return map;
    }


    // 댓글 수정
    public int updateComment(CommentDto commentDto) {
        int boardType = commentDto.getBoard_type();

        if (boardType == 1) {  // 자유게시판
            return boardMapper.updateComment01(commentDto.getComment_content(),
                    commentDto.getBoard_id(),
                    commentDto.getComment_id());
        } else if (boardType == 2) {  // 하소연 게시판
            return boardMapper.updateComment02(commentDto.getComment_content(),
                    commentDto.getBoard_id(),
                    commentDto.getComment_id());
        } else {
            return 0;  // 존재하지 않는 boardType일 경우 실패 처리
        }
    }

    // 게시글 삭제
    public void deleteBoard01(int board_id) {
        boardMapper.deleteBoardComment01(board_id);
        boardMapper.deleteBoardLike01(board_id);
        boardMapper.deleteBoardDislike01(board_id);
        boardMapper.deleteBoard01(board_id);
    }

    public void deleteBoard02(int board_id) {
        boardMapper.deleteBoardComment02(board_id);
        boardMapper.deleteBoardLike02(board_id);
        boardMapper.deleteBoardDislike02(board_id);
        boardMapper.deleteBoard02(board_id);
    }

    public boolean isLike01(int board_id) {
        String isLike = boardMapper.isLike01(board_id);
        if (isLike != null) {
            return true;
        } else {
            return false;
        }
    }

    public boolean isDislike01(int board_id) {
        String isLike = boardMapper.isDislike01(board_id);
        if (isLike != null) {
            return true;
        } else {
            return false;
        }
    }

    public boolean isLike02(int board_id) {
        String isLike = boardMapper.isLike02(board_id);
        if (isLike != null) {
            return true;
        } else {
            return false;
        }
    }

    public boolean isDislike02(int board_id) {
        String isLike = boardMapper.isDislike02(board_id);
        if (isLike != null) {
            return true;
        } else {
            return false;
        }
    }
}