package com.izikgram.board.service;

import com.izikgram.board.entity.Board;
import com.izikgram.board.repository.BoardMapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class BoardService {

    @Autowired
    private BoardMapper boardMapper;

    //게시판 종류 조회
    public String findBoardName(int board_type){
        String boardName = boardMapper.getBoardName(board_type);
        System.out.println(boardName);
        return boardName;
    }

    // 게시판 리스트 조회
    public List<Board> findBoardList(int board_type) {
        List<Board> boards = boardMapper.selectBoardList(board_type);
        System.out.println("Boards retrieved: " + boards);  // 리스트 내용 확인

        // 각 게시판의 reg_date를 "몇 분 전" 형태로 변환하여 바로 넣기
        for (Board board : boards) {
            String formattedTime = formatTimeDifference(board.getReg_date());
            board.setReg_date(formattedTime);  // reg_date 필드를 변형된 시간으로 설정
            System.out.println("Board - " + formattedTime);
        }

        return boards;
    }

    //작성글 삽입
    public void insertPost(int board_type, String writer_id, String title, String content) {

        boardMapper.insertPost(writer_id, title, content, board_type);
    }

    //게시글 상세보기
    public Board selectDetail(int board_id, int board_type){
        Board board = boardMapper.selectBoard(board_id, board_type);

        // 각 게시판의 reg_date를 "몇 분 전" 형태로 변환하여 바로 넣기

        String formattedTime = formatTimeDifference(board.getReg_date());
        board.setReg_date(formattedTime);  // reg_date 필드를 변형된 시간으로 설정
        System.out.println("Board - " + formattedTime);

        return board;
    }

    //자유/하소연 게시글 업데이트
    public boolean modifyBoard(int board_id, String title, String content, int board_type) {
        int result = 0;

        if (board_type == 1) {
            System.out.println("수정하는 타입(자유게시판) : " + title + " " + content);
            result = boardMapper.updateFreeBoard(board_id, title, content);
        } else if (board_type == 2) {
            System.out.println("수정하는 타입(하소연게시판) : " + title + " " + content);
            result = boardMapper.updateDiscontentBoard(board_id, title, content);
        } else {
            System.out.println("잘못된 board_type 값: " + board_type);
            return false;
        }

        return result > 0; // 영향받은 행이 1개 이상이면 true 반환
    }



    // reg_date를 "몇 분 전"으로 변환하는 메서드
    private String formatTimeDifference(String regDateStr) {
        try {
            // reg_date를 LocalDateTime으로 변환
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"); // DB에서 받아오는 포맷에 맞게 설정
            LocalDateTime regDate = LocalDateTime.parse(regDateStr, formatter); // String -> LocalDateTime으로 변환
            LocalDateTime now = LocalDateTime.now(); // 현재 시간

            // 시간 차이 계산
            Duration duration = Duration.between(regDate, now);
            long minutes = duration.toMinutes();

            if (minutes < 1) {
                return "방금 전"; // 1분 미만은 "방금 전"으로 처리
            } else if (minutes < 60) {
                return minutes + "분 전"; // 60분 미만은 "몇 분 전"
            } else if (minutes < 1440) { // 60 * 24 (하루 이내)
                long hours = duration.toHours();
                return hours + "시간 전";
            } else {
                long days = duration.toDays();
                return days + "일 전"; // 하루 이상은 "몇 일 전"
            }
        } catch (Exception e) {
            e.printStackTrace(); // 예외 발생 시 로깅
            return "시간 오류"; // 예외 발생 시 반환할 기본 값
        }
    }

}
