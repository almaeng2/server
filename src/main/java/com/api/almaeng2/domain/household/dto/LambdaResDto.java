package com.api.almaeng2.domain.household.dto;

import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class LambdaResDto {

    private String statusCode;
    private LambdaResHeaders headers;
    private String body;
}
