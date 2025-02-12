package com.iz.repository;

import com.iz.entity.User;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface UserMapper {

    @Insert("insert into iz_member" +
            "(name, member_id, password, phone_num, birth_date, email, gender, payday, start_time, end_time, lunch_time, loc_mod, ind_cd, edu_lv)" +
            "values (" +
            "#{name}, #{member_id}, #{password}, #{phone_num}, #{birth_date}, #{email}, #{gender}," +
            "#{payday}, #{start_time}, #{end_time}, #{lunch_time}, #{loc_mod}, #{ind_cd}, #{edu_lv})")
    void insertUser(User user);

    @Select("SELECT NOW()")
    String getCurrentTime();
}
