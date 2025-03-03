package com.izikgram.user.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserStressDTO {
    private String member_id;
    private int stress_num;
}
