package com.api.almaeng2.global.security.jwt;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
@AllArgsConstructor
public class TokenInfo {

    private String accessToken;
    private String refreshToken;

    private String name;
}
