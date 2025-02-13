package com.izikgram.job.repository;

import com.izikgram.user.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface JobMapper {


    @Select("select * from iz_member where member_id=#{member_id}")
    User getUserById(@Param("member_id") String member_id);



}
