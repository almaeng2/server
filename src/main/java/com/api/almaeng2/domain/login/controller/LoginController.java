package com.api.almaeng2.domain.login.controller;

import com.api.almaeng2.domain.login.dto.JoinReqDto;
import com.api.almaeng2.domain.login.dto.LoginReq;
import com.api.almaeng2.domain.login.service.LoginService;
import com.api.almaeng2.global.security.jwt.TokenInfo;
import com.api.almaeng2.global.success.SuccessResponse;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api/v1/auth")
public class LoginController {

    private final LoginService loginService;

    @Operation(summary = "회원가입", description = "사용자의 회원가입을 진행합니다.")
    @PostMapping(value = "/signup")
    public SuccessResponse register(@RequestBody JoinReqDto joinReqDto){

        loginService.doJoin(joinReqDto);

        return SuccessResponse.ok("회원가입이 완료되었습니다!");
    }

    @Operation(summary = "로그인", description = "사용자가 입력한 아이디와 비밀번호를 바탕으로 로그인을 진행합니다.")
    @PostMapping(value = "/signIn")
    public SuccessResponse<TokenInfo> signIn(HttpServletRequest request, HttpServletResponse response, @RequestBody LoginReq loginReq){

        TokenInfo tokenInfo = loginService.signIn(request, loginReq);

        return new SuccessResponse(tokenInfo);
    }

    @Operation(summary = "토큰 재발행", description = "액세스 토큰이 만료되었을 때 리프레시 토큰을 이용하여 재발급 받습니다.")
    @PatchMapping("/reissue")
    public SuccessResponse reissue(HttpServletRequest request, HttpServletResponse response){
        return new SuccessResponse(loginService.reissue(request));
    }

    @Operation(summary = "로그아웃", description = "로그아웃합니다.")
    @PostMapping("/logout")
    public SuccessResponse logout(HttpServletRequest request, HttpServletResponse response){
        return new SuccessResponse(loginService.doLogout(request));
    }
}
