package com.stone.common.result;

import lombok.Getter;

@Getter
public enum ResultCodeEnum {
    SUCCESS(200,"success"),//每一个枚举常量都是枚举类的一个实例，这里使用了类的私有的有参构造方法对枚举常量赋值。
    Fail(211,"fail"),
    LOGIN_ERROR(208,"认证失败");
    private Integer code;
    private String message;
    private ResultCodeEnum(Integer code, String message){
        this.code = code;
        this.message = message;
    }

}
