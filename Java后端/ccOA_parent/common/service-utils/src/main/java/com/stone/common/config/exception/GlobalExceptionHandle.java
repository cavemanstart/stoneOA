package com.stone.common.config.exception;


import com.stone.common.result.Result;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

@ControllerAdvice
public class GlobalExceptionHandle {
    //全局异常处理
    @ExceptionHandler(Exception.class)
    @ResponseBody
    public Result errorHandle(Exception e){
        e.printStackTrace();
        return Result.fail().message("执行全局异常方法");
    }
    //特定异常处理
    @ExceptionHandler(ArithmeticException.class)
    @ResponseBody
    public Result errorHandle(ArithmeticException e){
        e.printStackTrace();
        return Result.fail().message("执行特定异常方法");
    }
    /**
     * spring security异常
     * @param e
     * @return
     */
    @ExceptionHandler(AccessDeniedException.class)
    @ResponseBody
    public Result error(AccessDeniedException e) throws AccessDeniedException {
        e.printStackTrace();
        return Result.fail().code(231).message("没有操作权限");
    }

    //自定义异常处理
    @ExceptionHandler(StoneException.class)
    @ResponseBody
    public Result errorHandle(StoneException e){
        e.printStackTrace();
        return Result.fail().code(e.getCode()).message(e.getMsg());
    }
}
