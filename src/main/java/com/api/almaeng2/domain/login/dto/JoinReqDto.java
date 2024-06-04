package com.api.almaeng2.domain.login.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class JoinReqDto {

    private String userId;
    private String pw;
    private String username;

    private String level;

    private String birth;
}
