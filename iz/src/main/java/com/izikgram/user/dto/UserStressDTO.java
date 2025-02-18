package com.izikgram.user.dto;

import lombok.Data;

@Data
public class UserStressDTO {
    private String member_id;
    private int stress_num;

    public UserStressDTO(String member_id, int stress_num) {
        this.member_id = member_id;
        this.stress_num = stress_num;
    }
}
