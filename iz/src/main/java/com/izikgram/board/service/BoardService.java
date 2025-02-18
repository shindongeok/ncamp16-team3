package com.izikgram.board.service;

import com.izikgram.board.entity.Board;

import com.izikgram.board.entity.CommentDto;
import com.izikgram.board.repository.BoardMapper;
import lombok.extern.slf4j.Slf4j;
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

    //게시판 종류 조회
    public String findBoardName(int board_type){

        String boardName = boardMapper.getBoardName(board_type);

        return boardName;
    }

    // 게시판 리스트 조회
    public List<Board> SelectBoardList01(int board_type) {
        return boardMapper.getBoard01(board_type);
    }

    public List<Board> SelectBoardList02(int board_type) {
       return boardMapper.getBoard02(board_type);
    }



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

    // 댓글리스트 조회
    public List<CommentDto> selectComment01(int board_id ){
            return boardMapper.commentGetList01(board_id);
    }

    public List<CommentDto> selectComment02(int board_id){
        return boardMapper.commentGetList02(board_id);
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

}
