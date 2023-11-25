package com.stone.common.result;

import com.baomidou.mybatisplus.extension.api.R;
import lombok.Data;
import org.apache.ibatis.builder.BuilderException;
import org.omg.PortableServer.THREAD_POLICY_ID;

@Data
public class Result<T>{
    private Integer code;
    private String message;
    private T data;
    private Result(){}
    public static <T> Result<T> build(T body, ResultCodeEnum codeEnum){
        Result<T> result = new Result<>();
        if(body!=null){
            result.setData(body);
        }
        result.setCode(codeEnum.getCode());
        result.setMessage(codeEnum.getMessage());
        return result;
    }
    public static <T> Result<T> ok(){//静态方法需要定义为泛型方法才能使用泛型
        return build(null,ResultCodeEnum.SUCCESS);
    }
    public static<T> Result ok(T body){
        return build(body,ResultCodeEnum.SUCCESS);
    }
    public static <T> Result<T> fail(){
        return build(null, ResultCodeEnum.Fail);
    }
    public static <T> Result<T> fail(T body){
        return build(body, ResultCodeEnum.Fail);
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
