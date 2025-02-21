package com.izikgram.job.repository;

import com.izikgram.job.entity.Job;
import com.izikgram.user.entity.User;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface JobMapper {


    @Select("select * from iz_member where member_id=#{member_id}")
    User getUserById(@Param("member_id") String member_id);

    @Select("SELECT * FROM job " +
            "WHERE loc_mcd = #{loc_mcd} " +
            "AND ind_cd = #{ind_cd} " +
            "AND edu_lv = #{edu_lv}")
    List<Job> findByConditions(
            @Param("loc_mcd") String loc_mcd,
            @Param("ind_cd") String ind_cd,
            @Param("edu_lv") String edu_lv
    );

    @Select("SELECT * FROM job WHERE job_rec_id = #{job_rec_id}")
    Job findByJobRecId(@Param("job_rec_id") String job_rec_id);

    @Insert("INSERT INTO job (member_id, job_rec_id, title, company, " +
            "loc_mcd, ind_cd, edu_lv, deadline) " +
            "VALUES (#{member_id}, #{job_rec_id}, #{title}, #{company}, " +
            "#{loc_mcd}, #{ind_cd}, #{edu_lv}, #{deadline})")
    void insert(Job job);
}
