package com.api.almaeng2.global.security.entity;

import jakarta.persistence.Id;
import lombok.Builder;
import lombok.Getter;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.TimeToLive;

@Getter
@Builder
@RedisHash("refreshToken")
public class RefreshToken {

    @Id
    private String id;

    private String token;
    private String ip;

    @TimeToLive
    private Long expiration;
}
