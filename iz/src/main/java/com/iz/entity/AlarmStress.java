package com.iz.entity;

import lombok.Data;

@Data
public class AlarmStress {
    /**
     * Stress랑 비슷한데
     * 이걸 의도하신건지..?
     */
    private String member_id;
    private int stress_num;

}
