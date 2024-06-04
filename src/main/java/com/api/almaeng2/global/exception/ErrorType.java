package com.api.almaeng2.global.exception;

import lombok.Getter;
import lombok.ToString;
import org.springframework.http.HttpStatus;

import static org.springframework.http.HttpStatus.*;

@Getter
@ToString
public enum ErrorType {

    // ---------------------- S3 ----------------------------
    EXCEEDING_FILE_COUNT(BAD_REQUEST, "S4001", "사진 개수가 너무 많습니다."),
    S3_UPLOAD(INTERNAL_SERVER_ERROR, "S5001", "서버오류, S3 사진 업로드 에러입니다."),
    S3_CONNECT(INTERNAL_SERVER_ERROR, "S5002", "서버오류, S3연결 에러입니다."),
    S3_CONVERT(INTERNAL_SERVER_ERROR, "S5003", "서버오류, S3 변환 에러입니다."),
    S3_INVALID_DIRECTORY(BAD_REQUEST, "S5004", "유효하지 않은 경로입니다."),
    NO_FILE_CONTAINED(BAD_REQUEST, "S4005", "사진이 없습니다."),

    // ---------------------- Auth --------------------------

    DUPLICATE_ID(BAD_REQUEST, "A4001", "중복되는 아이디입니다."),
    NOT_FOUND_BY_TOKEN(NOT_FOUND, "A4002", "요구된 토큰에서 사용자를 찾지 못하였습니다."),
    NOT_FOUND_USER(NOT_FOUND, "A4003", "로그인 정보가 일치하지 않습니다."),
    _UNAUTHORIZED(UNAUTHORIZED, "A4004", "승인받지 못하였습니다."),

    // ---------------------- JwtToken ---------------------------
    _JWT_PARSING_ERROR(BAD_REQUEST, "JWT_4001", "JWT Token이 올바르지 않습니다."),
    _JWT_EXPIRED(UNAUTHORIZED, "JWT_4010", "Jwt Token의 유효 기간이 만료되었습니다."),
    TOKEN_NOT_REQUIRED(BAD_REQUEST, "JWT_4003", "토큰을 재발급할 필요가 없습니다."),
    _NOT_FOUND_REFRESHTOKEN(NOT_FOUND, "JWT_4004", "요구된 리프레쉬 토큰이 없습니다."),

    // ------------------------- HousePill -----------------------------------------------

    _NOT_CORRECT_DIRECTION(BAD_REQUEST, "H4001", "약품을 뒤집어 다시 촬영해 주세요."),

    // --------------------- Precription -------------------------
    _NOT_FOUND_PRESCRIPTION(NOT_FOUND, "P4001", "해당 처방전을 찾을 수 없습니다."),
    _NOT_FOUND_CLASS(NOT_FOUND, "P4002", "해당 처방전의 분류를 찾을 수 없습니다."),
    _NOT_FOUND_PILL(BAD_REQUEST, "P4003", "해당 약품을 찾을 수 없습니다."),
    _NOT_MINE(BAD_REQUEST, "P4004", "볼 수 있는 권한이 없습니다.");

    private final HttpStatus status;
    private final String errorCode;
    private final String msg;

    ErrorType(HttpStatus status, String errorCode, String msg) {
        this.status = status;
        this.errorCode = errorCode;
        this.msg = msg;
    }

    public HttpStatus getStatus() {
        return status;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public String getMsg() {
        return msg;
    }
}
