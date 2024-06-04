package com.api.almaeng2.domain.household.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

@Getter
public class LambdaResHeaders {

    @JsonProperty(value = "Content-Type")
    private String type;
}
