package com.chloe.common;

import lombok.Getter;

@Getter
public enum ResultCodeEnum {
    SUCCESS(200, "success"),
    FAIL(201, "failed"),
    LOGIN_MOBLE_ERROR(208, "login error"),
    PERMISSION(205, "permission");

    private Integer code;
    private String message;

    private ResultCodeEnum(Integer code, String message){
        this.code = code;
        this.message = message;
    }

}
