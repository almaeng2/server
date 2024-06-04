package com.api.almaeng2.domain.member.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Getter;

@Getter
public enum Level {

    NON_BLIND, LOW_VISION, TOTALLY_BLIND
}
