package com.api.almaeng2.domain.login.controller;

import com.api.almaeng2.domain.login.dto.JoinReqDto;
import com.api.almaeng2.domain.login.service.LoginService;
import com.api.almaeng2.global.success.SuccessResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api/v1/auth")
public class LoginController {

    private final LoginService loginService;

    @PostMapping(value = "/register")
    public SuccessResponse register(@RequestBody JoinReqDto joinReqDto){

        loginService.doJoin(joinReqDto);

        return SuccessResponse.ok("회원가입이 완료되었습니다!");
    }
}
