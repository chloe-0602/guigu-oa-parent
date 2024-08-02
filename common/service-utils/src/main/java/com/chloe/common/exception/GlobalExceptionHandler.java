package com.chloe.common.exception;

import com.chloe.common.Result;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(OaException.class)
    @ResponseBody
    public Result error(OaException e){
        e.printStackTrace();
        return Result.fail().code(e.getCode()).message(e.getMessage());
    }

    @ExceptionHandler(ArithmeticException.class)
    @ResponseBody
    public Result error(ArithmeticException e){
        e.printStackTrace();
        return Result.fail().message("执行全局ArithmeticException错误: "  + e.getMessage());
    }

    @ExceptionHandler(Exception.class)
    @ResponseBody
    public Result error(Exception e){
        e.printStackTrace();
        return Result.fail().message("执行全局异常错误: "  + e.getMessage());
    }

    @ExceptionHandler(AccessDeniedException.class)
    @ResponseBody
    public Result error(AccessDeniedException e){
        e.printStackTrace();
        return Result.fail().message("执行全局AccessDeniedException错误， 没有相关的权限: "  + e.getMessage());
    }
}
