package com.izikgram.user.dto;

import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class PasswordDTO {

    @Pattern(regexp = "^(?=.*?[a-z])(?=.*?[0-9]).{8,15}$", message = "비밀번호는 8~15자 사이이며, 영문소문자와 숫자가 필수입니다")
    private String password;

}
