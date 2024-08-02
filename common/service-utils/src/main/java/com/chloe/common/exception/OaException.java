package com.chloe.common.exception;

import com.chloe.common.ResultCodeEnum;
import lombok.Data;

@Data
public class OaException extends RuntimeException{
    private Integer code;
    private String message;

    public OaException(Integer code, String message){
        super(message);
        this.code = code;
        this.message = message;
    }

    public OaException(ResultCodeEnum resultCodeEnum){
        super(resultCodeEnum.getMessage());
        this.code = resultCodeEnum.getCode();
        this.message = resultCodeEnum.getMessage();
    }
}
