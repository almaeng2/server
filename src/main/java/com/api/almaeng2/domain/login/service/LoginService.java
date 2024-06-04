package com.api.almaeng2.domain.login.service;

import com.amazonaws.Response;
import com.api.almaeng2.domain.base.Helper;
import com.api.almaeng2.domain.login.dto.JoinReqDto;
import com.api.almaeng2.domain.login.dto.LoginReq;
import com.api.almaeng2.domain.member.entity.Level;
import com.api.almaeng2.domain.member.entity.Member;
import com.api.almaeng2.domain.member.entity.Role;
import com.api.almaeng2.domain.member.repository.MemberRepository;
import com.api.almaeng2.global.exception.ApiException;
import com.api.almaeng2.global.exception.ErrorType;
import com.api.almaeng2.global.security.entity.RefreshToken;
import com.api.almaeng2.global.security.jwt.JwtProvider;
import com.api.almaeng2.global.security.jwt.TokenInfo;
import com.api.almaeng2.global.security.repository.RefreshTokenRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.cglib.core.Local;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Date;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Slf4j
@RequiredArgsConstructor
@Service
public class LoginService {

    private final MemberRepository memberRepository;
    private final JwtProvider jwtProvider;
    private final RedisTemplate<String, Object> redisTemplate;
    private final RefreshTokenRepository refreshTokenRepository;
    private final Long refreshTokenExpiration = 1000L * 60 * 60 * 24 * 7; // 1 week

    // ------------------------------------- sign up ---------------------------------------

    public ResponseEntity<?> doJoin(JoinReqDto joinReqDto){

        String isDuplicated = memberRepository.findByUserId(joinReqDto.getUserId());

        log.info("{}, {}", isDuplicated, joinReqDto.getUserId());
        if(isDuplicated!=null) {
            if (isDuplicated.equals(joinReqDto.getUserId()))
                throw new ApiException(ErrorType.DUPLICATE_ID);
        }
        memberRepository.findByDuplicateUserId(joinReqDto.getUserId()).orElseGet(() -> memberRepository.save(Member.builder()
                .userId(joinReqDto.getUserId())
                .password(joinReqDto.getPw())
                .level(validateLevel(joinReqDto.getLevel()))
                .username(joinReqDto.getUsername())
                .birth(validateBirth(joinReqDto.getBirth()))
                .role(Role.ROLE_USER)
                .build()));

        return ResponseEntity.ok("환영합니다!");
    }

    private Level validateLevel(String lev){
        int level = Integer.parseInt(lev);
        if(level == 1)
            return Level.NON_BLIND;
        else if(level == 2)
            return Level.LOW_VISION;
        else return Level.TOTALLY_BLIND;
    }

    private LocalDate validateBirth(String birth){
        int year = Integer.parseInt(birth.substring(0,4));
        int month = Integer.parseInt(birth.substring(5,7));
        int day = Integer.parseInt(birth.substring(8,10));

        LocalDate birthday = LocalDate.of(year, month, day);

        return birthday;
    }

    // ------------------------------------ login --------------------------------------

    public TokenInfo signIn(HttpServletRequest request, LoginReq loginRequest){

        TokenInfo tokenInfo = null;

        Member member = memberRepository.findByDuplicateUserId(loginRequest.getId()).orElseThrow(() -> new ApiException(ErrorType.NOT_FOUND_USER));
        if(member.getPassword().equals(loginRequest.getPw()))
            tokenInfo = jwtProvider.createToken(loginRequest.getId());
        else{
            throw new ApiException(ErrorType.NOT_FOUND_USER);
        }

        log.info("id" + member.getUserId() + "pw " + member.getPassword());
        tokenInfo.setName(member.getUsername());

        redisTemplate.opsForValue()
                        .set("RefreshToken:" + member.getUserId(), tokenInfo.getRefreshToken(),
                                tokenInfo.getRefreshTokenExpiration() - new Date().getTime(), TimeUnit.MILLISECONDS);

        refreshTokenRepository.save(RefreshToken.builder()
                .id(member.getUserId())
                .token(tokenInfo.getRefreshToken())
                .ip(Helper.getClientIp(request))
                .expiration(tokenInfo.getRefreshTokenExpiration())
                .build());

        return tokenInfo;
    }

    // ------------------------------------ reissue -------------------------------------

    public Object reissue(HttpServletRequest request){
        String accessToken = jwtProvider.resolveAccessToken(request);
        String refreshToken = jwtProvider.resolveRefreshToken(request);

        if(!jwtProvider.isExpired(accessToken)){
            throw new ApiException(ErrorType.TOKEN_NOT_REQUIRED);
        }

        jwtProvider.validateToken(refreshToken);
        if (jwtProvider.isRefreshToken(refreshToken)) {
            Member member = memberRepository.findByDuplicateUserId(jwtProvider.getPK(refreshToken)).orElseThrow(() -> new ApiException(ErrorType.NOT_FOUND_USER));
            String currentIp = Helper.getClientIp(request);
            RefreshToken refreshToken1 = refreshTokenRepository.findById(member.getUserId()).orElseThrow(() -> new ApiException(ErrorType._NOT_FOUND_REFRESHTOKEN));
            if(refreshToken1.getIp().equals(currentIp)){
                TokenInfo tokenInfo = jwtProvider.createToken(member.getUserId());
                redisTemplate.opsForValue()
                        .set("RefreshToken:" + member.getUserId(), tokenInfo.getRefreshToken(),
                                tokenInfo.getRefreshTokenExpiration() - new Date().getTime(), TimeUnit.MILLISECONDS);

                refreshTokenRepository.save(RefreshToken.builder()
                        .id(member.getUserId())
                        .token(tokenInfo.getRefreshToken())
                        .ip(Helper.getClientIp(request))
                        .expiration(tokenInfo.getRefreshTokenExpiration())
                        .build());

                return tokenInfo;
            }
        }

        throw new ApiException(ErrorType._JWT_PARSING_ERROR);
    }

    // ------------------------------------ logout --------------------------------------


    public ResponseEntity<?> doLogout(HttpServletRequest request){
        String accessToken = jwtProvider.resolveAccessToken(request);
        log.info("accessToken: " + accessToken);
        jwtProvider.validateToken(accessToken);

        Long expiration = jwtProvider.getExpiration(accessToken);
        String id = jwtProvider.getPK(accessToken);

        if(redisTemplate.opsForValue().get("RefreshToken:" + id) != null){
            redisTemplate.delete("RefreshToken:" + id);
        }
        redisTemplate.opsForValue().set(accessToken, "logout", expiration, TimeUnit.MILLISECONDS);
        refreshTokenRepository.deleteById(id);

        return ResponseEntity.ok("로그아웃되었습니다.");
    }

}
