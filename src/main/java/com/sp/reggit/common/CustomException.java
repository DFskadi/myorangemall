package com.sp.reggit.common;

/**
 * 自定义业务异常
 */
public class CustomException extends RuntimeException{
    public CustomException(String message){
        super(message);//message 被父类封装成private的了，不能直接访问所以使用这种形式
    }
}
