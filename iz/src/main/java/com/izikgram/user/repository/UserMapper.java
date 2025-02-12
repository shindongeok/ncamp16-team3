package com.izikgram.user.repository;

import com.izikgram.user.entity.User;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface UserMapper {

    @Insert("insert into iz_member" +
            "(name, member_id, password, phone_num, birth_date, email, gender, payday, start_time, end_time, lunch_time, loc_mod, ind_cd, edu_lv)" +
            "values (" +
            "#{name}, #{member_id}, #{password}, #{phone_num}, #{birth_date}, #{email}, #{gender}," +
            "#{payday}, #{start_time}, #{end_time}, #{lunch_time}, #{loc_mod}, #{ind_cd}, #{edu_lv})")
    void insertUser(User user);

    @Select("select * from iz_member where member_id=#{member_id} and password=#{password}")
    User login(@Param("member_id") String member_id, @Param("password") String password);

    @Select("select * from iz_member where member_id=#{member_id}")
    User findUserById(@Param("member_id") String member_id);

    @Select("SELECT NOW()")
    String getCurrentTime();

    @Select("select * from iz_member where member_id=#{member_id}")
    User getUserInfo(@Param("member_id") String member_id);
}
