package com.api.almaeng2.global.util;

import com.api.almaeng2.domain.member.entity.Member;
import com.api.almaeng2.global.exception.ApiException;
import com.api.almaeng2.global.exception.ErrorType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

@Slf4j
public class SecurityUtil {

    public static Member currentMember(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        log.info("authentication: " + authentication.getName());

        if(authentication == null || !authentication.isAuthenticated()){
            throw new ApiException(ErrorType._UNAUTHORIZED);
        }

        Object principal = authentication.getPrincipal();

        return (Member) principal;
    }
}
