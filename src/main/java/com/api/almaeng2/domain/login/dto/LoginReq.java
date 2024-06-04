package com.api.almaeng2.domain.login.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.annotations.NotFound;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class LoginReq {

    @NotNull
    private String id;

    @NotNull
    private String pw;
}
