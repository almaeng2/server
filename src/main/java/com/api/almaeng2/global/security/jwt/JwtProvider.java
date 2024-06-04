package com.api.almaeng2.global.security.jwt;

import com.api.almaeng2.domain.member.entity.Member;
import com.api.almaeng2.domain.member.entity.Role;
import com.api.almaeng2.domain.member.repository.MemberRepository;
import com.api.almaeng2.global.exception.ApiException;
import com.api.almaeng2.global.exception.ErrorType;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import io.micrometer.core.instrument.config.validate.Validated;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Component
@Slf4j
@RequiredArgsConstructor
public class JwtProvider {

    private final MemberRepository memberRepository;
    private final String BEARER_FREFIX = "Bearer ";
    private final long tokenExpiration = 1000L * 60 * 60; // 1 hour
    private final long refreshTokenExpiration = 1000L * 60 * 60 * 24 * 7; // 1 week

    @Value("${spring.jwt.secret}")
    private String key;

    public TokenInfo createToken(String id){

        Date now =  new Date();

        return TokenInfo.builder()
                .accessToken(generateToken(id, tokenExpiration, "access"))
                .refreshToken(generateToken(id, refreshTokenExpiration, "refresh"))
                .refreshTokenExpiration(now.getTime() + refreshTokenExpiration)
                .build();
    }

    public String generateToken(final String id, long time, String type){
        Claims claims = createClaim(id);
        Date now = new Date();
        SecretKey secretKey = generateKey();

        String token = BEARER_FREFIX + Jwts.builder()
                .claim("type", type)
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(new Date(now.getTime() + time))
                .signWith(secretKey, SignatureAlgorithm.HS256)
                .compact();

        return token;
    }

    public Claims createClaim(String id){
        return Jwts.claims().setSubject(id);
    }

    public SecretKey generateKey(){
        return Keys.hmacShaKeyFor(key.getBytes(StandardCharsets.UTF_8));
    }

    public void validateToken(String token){

        log.info("jwtToken: " + token);

        try{
            SecretKey key = generateKey();
            Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token);
        } catch(ExpiredJwtException e){
            throw new ApiException(ErrorType._JWT_EXPIRED);
        } catch(Exception e){
            throw new ApiException(ErrorType._JWT_PARSING_ERROR);
        }
    }

    public Authentication getAuthentication(String jwtToken) {
        SecretKey key = generateKey();

        Claims claims = Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(jwtToken)
                .getBody();

        String userId = claims.getSubject();
        Member member = memberRepository.findByDuplicateUserId(userId).orElseThrow(() -> new ApiException(ErrorType.NOT_FOUND_BY_TOKEN));

        Role role = member.getRole();
        List<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority("ROLE_" + role.name()));

        return new UsernamePasswordAuthenticationToken(member, jwtToken, authorities);
    }

    // ------------------- resolve Token ----------------------------------------------

    public String resolveAccessToken(HttpServletRequest request){
        String token = request.getHeader("Authorization");
        if(!ObjectUtils.isEmpty(token) && token.startsWith("Bearer ")){
            return token.substring(7);
        }

        return null;
    }

    public String resolveRefreshToken(HttpServletRequest httpServletRequest){
        String token = httpServletRequest.getHeader("refreshToken");
        if(!ObjectUtils.isEmpty(token) && token.startsWith("Bearer ")){
            return token.substring(7);
        }
        return null;
    }

    public Boolean isRefreshToken(String token){
        SecretKey key = generateKey();

        String type= (String) Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody().get("type");

        return type.equals("refresh");
    }

    public Boolean isExpired(String token){
        SecretKey key = generateKey();
        Date expiration = Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody().getExpiration();

        if(expiration.getTime() - new Date().getTime() > 0){ // 토큰 재발급할 필요 없음.
            return false;
        }
        return true;
    }

    public Long getExpiration(String token){
        SecretKey key = generateKey();
        Date expiration = Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody().getExpiration();

        long now = new Date().getTime();
        return expiration.getTime()-now;
    }

    public String getPK(String token){
        SecretKey key = generateKey();
        Claims claims = Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody();
        return claims.getSubject();
    }
}
