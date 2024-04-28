package com.api.almaeng2.domain.login.service;

import com.api.almaeng2.domain.login.dto.JoinReqDto;
import com.api.almaeng2.domain.member.entity.Level;
import com.api.almaeng2.domain.member.entity.Member;
import com.api.almaeng2.domain.member.entity.Role;
import com.api.almaeng2.domain.member.repository.MemberRepository;
import com.api.almaeng2.global.exception.ApiException;
import com.api.almaeng2.global.exception.ErrorType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cglib.core.Local;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Service
public class LoginService {

    private final MemberRepository memberRepository;

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

}
