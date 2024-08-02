package com.chloe.common;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
public class Result<T> {
    private Integer code;
    private String message;

    private T data;

    private Result() {}

    public static <T> Result<T> build(T data){
        Result<T> result = new Result<>();
        if(data != null){
            result.setData(data);
        }
        return result;
    }
    public static <T> Result<T> build(T body, ResultCodeEnum resultCodeEnum){
        Result<T> result = build(body);
        result.setCode(resultCodeEnum.getCode());
        result.setMessage(resultCodeEnum.getMessage());
        return result;
    }

    public static <T> Result<T> build(T body, Integer code, String message){
        Result<T> result = build(body);
        result.setCode(code);
        result.setMessage(message);
        return result;
    }

    public static<T> Result<T> ok(T data){
        Result<T> result = build(data);
        return build(data, ResultCodeEnum.SUCCESS);
    }

    public static<T> Result<T> ok(){
        return Result.ok(null);
    }

    public static<T> Result<T> fail(T data){
        Result<T> result = build(data);
        return build(data, ResultCodeEnum.FAIL);
    }

    public static<T> Result<T> fail(){
        return Result.fail(null);
    }

    public Result<T> message(String msg){
        this.setMessage(msg);
        return this;
    }

    public Result<T> code(Integer code){
        this.setCode(code);
        return this;
    }
}
