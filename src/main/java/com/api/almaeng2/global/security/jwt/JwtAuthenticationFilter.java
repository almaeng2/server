package com.api.almaeng2.global.security.jwt;

import com.api.almaeng2.global.exception.ApiException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@Slf4j
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtProvider jwtProvider;
    private final RedisTemplate<String, Object> redisTemplate;
    @Override
    protected void doFilterInternal(final HttpServletRequest request, final HttpServletResponse response, final FilterChain filterChain) throws ServletException, IOException {

        String requestURI = request.getRequestURI();
        if(isPublicUri(requestURI)){
            filterChain.doFilter(request, response);
            return;
        }

        log.info("----- Jwt filter do FilterInternal ------");
        String authorizationHeader = request.getHeader("Authorization");
        log.info("----- authorizationHeader =" + authorizationHeader);

        if(authorizationHeader != null && isBearer(authorizationHeader)){
            try{
                String jwtToken = authorizationHeader.substring(7);

                if(jwtToken != null){
                    jwtProvider.validateToken(jwtToken);

                    String isLogout = (String) redisTemplate.opsForValue().get(jwtToken);
                    if(ObjectUtils.isEmpty(isLogout)) {

                        Authentication authentication = jwtProvider.getAuthentication(jwtToken);
                        SecurityContextHolder.getContext().setAuthentication(authentication);
                    }
                }
            } catch(ApiException e){
                return;
            }
        }

        filterChain.doFilter(request, response);
    }

    private boolean isBearer(final String authorizationHeader){
        return authorizationHeader.startsWith("Bearer ");
    }

    private boolean isPublicUri(final String requestURI) {
        return
                requestURI.startsWith("/swagger-ui/**") ||
                requestURI.startsWith("/api/health") ||
                        requestURI.startsWith("/api/v1/auth/signup") ||
                        requestURI.startsWith("/api/v1/auth/signIn") ||
                        requestURI.startsWith("/api/v1/auth/reissue");
    }
}
