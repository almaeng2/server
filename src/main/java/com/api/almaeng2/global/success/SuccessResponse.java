package com.api.almaeng2.global.success;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

@AllArgsConstructor
@Getter
@Builder
@ToString
public class SuccessResponse<T> {

    private int status;
    private LocalDateTime time;
    private String code;
    private String msg;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private T result;

    public SuccessResponse(T result){
        this.result = result;
        this.status = HttpStatus.OK.value();
        this.time = LocalDateTime.now();
        this.code = SuccessResponseStatus.SUCCESS.getCode();
        this.msg = SuccessResponseStatus.SUCCESS.getMsg();
    }

    public SuccessResponse(T result, String msg){
        this.result = result;
        this.status = HttpStatus.OK.value();
        this.time = LocalDateTime.now();
        this.code = SuccessResponseStatus.SUCCESS.getCode();
        this.msg = msg;
    }

    public static SuccessResponse ok(){
        return SuccessResponse.builder()
                .status(HttpStatus.OK.value())
                .time(LocalDateTime.now())
                .code(SuccessResponseStatus.SUCCESS.getCode())
                .msg(SuccessResponseStatus.SUCCESS.getMsg())
                .build();
    }

    public static SuccessResponse ok(String msg){
        return SuccessResponse.builder()
                .status(HttpStatus.OK.value())
                .time(LocalDateTime.now())
                .code(SuccessResponseStatus.SUCCESS.getCode())
                .msg(msg)
                .build();
    }

}
