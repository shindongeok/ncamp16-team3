package com.izikgram.user.repository;

import com.izikgram.user.entity.User;
import org.apache.ibatis.annotations.*;

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

    @Select("select member_id from iz_member where name=#{name}")
    String findIdByName(String name);

    @Update("update iz_member set password=#{password} where member_id=#{member_id}")
    void updateUserPw(@Param("password") String password, @Param("member_id") String member_id);

    @Select("SELECT NOW()")
    String getCurrentTime();
}
