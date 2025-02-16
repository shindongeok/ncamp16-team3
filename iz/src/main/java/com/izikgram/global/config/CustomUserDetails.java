package com.izikgram.global.config;

import com.izikgram.user.entity.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

@Getter
@AllArgsConstructor
public class CustomUserDetails implements UserDetails {

     private User user;



//    // UserDetails 필수 필드
//    private final String username;
//    private final String password;
//    private final Collection<? extends GrantedAuthority> authorities;
//
//    // User 엔티티의 추가 필드들
//    private final String name;
//    private final String phone_num;
//    private final String nickname;
//    private final String birth_date;
//    private final String email;
//    private final int payday;
//    private final String gender;
//    private final String start_time;
//    private final String lunch_time;
//    private final String end_time;
//    private final String intro;
//    private final String loc_Mod;
//    private final String ind_Cd;
//    private final String edu_Lv;


//    public CustomUserDetails(User user) {
//        this.username = user.getMember_id();  // member_id를 username으로 사용
//        this.password = "{noop}" + user.getPassword(); // 암호화 안해놔서 일단 임시 처리
////        this.password = user.getPassword();
//        this.name = user.getName();
//        this.phone_num = user.getPhone_num();
//        this.nickname = user.getNickname();
//        this.birth_date = user.getBirth_date();
//        this.email = user.getEmail();
//        this.payday = user.getPayday();
//        this.gender = user.getGender();
//        this.start_time = user.getStart_time();
//        this.lunch_time = user.getLunch_time();
//        this.end_time = user.getEnd_time();
//        this.intro = user.getIntro();
//        this.loc_Mod = user.getLoc_mod();
//        this.ind_Cd = user.getInd_cd();
//        this.edu_Lv = user.getEdu_lv();
//
//        // 기본 권한 설정 (필요에 따라 수정)
//        this.authorities = Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"));
//    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of();
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        return user.getMember_id();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

}