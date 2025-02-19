package com.izikgram.board.repository;

import com.izikgram.board.entity.Board;
import com.izikgram.board.entity.Comment;
import com.izikgram.board.entity.CommentDto;
import org.apache.ibatis.annotations.*;

import java.util.List;


@Mapper
public interface BoardMapper {

    //게시판 종류
    @Select("select board_name " +
            "from iz_board_type " +
            "where board_type = #{board_type}")
    String getBoardName(@Param("board_type") int board_type);

    // 게시판 전체 조회
    @Select("select * " +
            "from iz_board_type t " +
            "join iz_board01 b " +
            "on t.board_type = b.board_type " +
            "where t.board_type = #{board_type} " +
            "order by board_id desc")
    List<Board> getBoard01(@Param("board_type") int board_type);

    @Select("select * " +
            "from iz_board_type t " +
            "join iz_board02 b " +
            "on t.board_type = b.board_type " +
            "where t.board_type = #{board_type} " +
            "order by board_id desc")
    List<Board> getBoard02(@Param("board_type") int board_type);

    // 글작성
    @Insert("insert into iz_board01(writer_id,title,content,board_type) " +
            "value(#{writer_id}, #{title}, #{content}, #{board_type})")
    void insertBoard01(@Param("writer_id") String writer_id, @Param("title") String title, @Param("content") String content,
                       @Param("board_type") int board_type);

    @Insert("insert into iz_board02(writer_id,title,content,board_type) " +
            "value(#{writer_id}, #{title}, #{content}, #{board_type})")
    void insertBoard02(@Param("writer_id") String writer_id, @Param("title") String title, @Param("content") String content,
                       @Param("board_type") int board_type);

    //상세페이지 보기
    @Select("SELECT * " +
            "FROM iz_board01  " +
            "WHERE board_id = #{board_id} ")
    Board selectBoard01(@Param("board_id") int board_id);

    @Select("SELECT * " +
            "FROM iz_board02 " +
            "WHERE board_id = #{board_id} ")
    Board selectBoard02(@Param("board_id") int board_id);

    // 댓글 리스트 조회
    @Select("select c.comment_id, c.board_id, c.writer_id, c.comment_content, c.reg_date, " +
            "m.nickname " +
            "from iz_board01_comment c " +
            "join iz_member m " +
            "on c.writer_id = m.member_id " +
            "where board_id = #{board_id} order by comment_id desc")
    List<CommentDto> commentGetList01(@Param("board_id") int board_id);

    @Select("select c.comment_id, c.board_id, c.writer_id, c.comment_content, c.reg_date, " +
            "m.nickname " +
            "from iz_board01_comment c " +
            "join iz_member m " +
            "on c.writer_id = m.member_id " +
            "where board_id = #{board_id} order by comment_id desc")
    List<CommentDto> commentGetList02(@Param("board_id") int board_id);

    //자유게시글 업데이트
    @Update("UPDATE iz_board01 " +
            "SET " +
            "    title = #{title}, " +
            "    content = #{content} " +
            "WHERE " +
            "    board_id = #{board_id} ")
    void updateBoard01(@Param("board_id") int board_id,
                       @Param("title") String title,
                       @Param("content")String content);

    //하소연게시글 업데이트
    @Update("UPDATE iz_board02 " +
            "SET " +
            "    title = #{title}, " +
            "    content = #{content} " +
            "WHERE " +
            "    board_id = #{board_id} ")
    void updateBoard02(@Param("board_id") int board_id,
                       @Param("title") String title,
                       @Param("content")String content);
    // ------------------------------


    // 회원의 게시글 좋아요 개수
    @Select("SELECT COUNT(*) FROM iz_board01_like WHERE board_id=#{board_id} and member_id=#{member_id}")
    int countLikeByMember(@Param("board_id") int boardId, @Param("member_id") String memberId);

    // 회원의 게시글 싫어요 개수
    @Select("SELECT COUNT(*) FROM iz_board01_dislike WHERE board_id=#{board_id} and member_id=#{member_id}")
    int countDislikeByMember(@Param("board_id") int boardId, @Param("member_id") String memberId);


    // 좋아요 생성
    @Insert("INSERT INTO iz_board01_like VALUES(#{board_id}, #{member_id})")
    void insertLike(@Param("board_id") int boardId, @Param("member_id") String memberId);

    // 싫어요 생성
    @Insert("INSERT INTO iz_board01_dislike VALUES(#{board_id}, #{member_id})")
    void insertDislike(@Param("board_id") int boardId, @Param("member_id") String memberId);

    // 좋아요 삭제
    @Delete("DELETE FROM iz_board01_like WHERE board_id=#{board_id} AND member_id=#{member_id}")
    void deleteLike(@Param("board_id") int boardId, @Param("member_id") String memberId);

    // 싫어요 삭제
    @Delete("DELETE FROM iz_board01_dislike WHERE board_id=#{board_id} AND member_id=#{member_id}")
    void deleteDislike(@Param("board_id") int boardId, @Param("member_id") String memberId);


    // TODO iz_board01의 like, dislike + 1, -1
    // 게시글 like_count +1
    @Update("update iz_board01 set like_count = like_count+1 where board_id = #{board_id}")
    void upLikeCount(@Param("board_id") int boardId);

    // 게시글 like_count -1
    @Update("update iz_board01 set like_count = like_count-1 where board_id = #{board_id}")
    void downLikeCount(@Param("board_id") int boardId);

    // 게시글 disLike_count +1
    @Update("update iz_board01 set dislike_count = dislike_count+1 where board_id = #{board_id}")
    void upDislikeCount(@Param("board_id") int boardId);

    // 게시글 disLike_count -1
    @Update("update iz_board01 set dislike_count = dislike_count-1 where board_id = #{board_id}")
    void downDislikeCount(@Param("board_id") int boardId);

    // TODO iz_board01의 like, dislike 개수 반환
    // 게시글의 좋아요/싫어요 전체 수
    @Select("select * from iz_board01 where board_id = #{board_id}")
    Board totalLikeDisLike(@Param("board_id") int boardId);
    //======================================================================

    // 회원의 게시글 좋아요 개수
    @Select("SELECT COUNT(*) FROM iz_board02_like WHERE board_id=#{board_id} and member_id=#{member_id}")
    int countLikeByMember02(@Param("board_id") int boardId, @Param("member_id") String memberId);

    // 회원의 게시글 싫어요 개수
    @Select("SELECT COUNT(*) FROM iz_board02_dislike WHERE board_id=#{board_id} and member_id=#{member_id}")
    int countDislikeByMember02(@Param("board_id") int boardId, @Param("member_id") String memberId);


    // 좋아요 생성
    @Insert("INSERT INTO iz_board02_like VALUES(#{board_id}, #{member_id})")
    void insertLike02(@Param("board_id") int boardId, @Param("member_id") String memberId);

    // 싫어요 생성
    @Insert("INSERT INTO iz_board02_dislike VALUES(#{board_id}, #{member_id})")
    void insertDislike02(@Param("board_id") int boardId, @Param("member_id") String memberId);

    // 좋아요 삭제
    @Delete("DELETE FROM iz_board02_like WHERE board_id=#{board_id} AND member_id=#{member_id}")
    void deleteLike02(@Param("board_id") int boardId, @Param("member_id") String memberId);

    // 싫어요 삭제
    @Delete("DELETE FROM iz_board02_dislike WHERE board_id=#{board_id} AND member_id=#{member_id}")
    void deleteDislike02(@Param("board_id") int boardId, @Param("member_id") String memberId);


    // TODO iz_board02의 like, dislike + 1, -1
    // 게시글 like_count +1
    @Update("update iz_board02 set like_count = like_count+1 where board_id = #{board_id}")
    void upLikeCount02(@Param("board_id") int boardId);

    // 게시글 like_count -1
    @Update("update iz_board02 set like_count = like_count-1 where board_id = #{board_id}")
    void downLikeCount02(@Param("board_id") int boardId);

    // 게시글 disLike_count +1
    @Update("update iz_board02 set dislike_count = dislike_count+1 where board_id = #{board_id}")
    void upDislikeCount02(@Param("board_id") int boardId);

    // 게시글 disLike_count -1
    @Update("update iz_board02 set dislike_count = dislike_count-1 where board_id = #{board_id}")
    void downDislikeCount02(@Param("board_id") int boardId);

    // TODO iz_board01의 like, dislike 개수 반환
    // 게시글의 좋아요/싫어요 전체 수
    @Select("select * from iz_board02 where board_id = #{board_id}")
    Board totalLikeDisLike02(@Param("board_id") int boardId);

    //댓글 저장
    @Insert("insert into iz_board01_commment(board_id, writer_id, comment_content) " +
            "values(#{board_id}, #{writer_id}, #{comment_content})")
    void addComment01(@Param("board_id") int board_id, @Param("writer_id") String writer_id,
                      @Param("comment_content") String comment_content);

    @Insert("insert into iz_board02_commment(board_id, writer_id, comment_content) " +
            "values(#{board_id}, #{writer_id}, #{comment_content})")
    void addComment02(@Param("board_id") int board_id, @Param("writer_id") String writer_id,
                      @Param("comment_content") String comment_content);
}
