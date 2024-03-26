package com.zxl.common;

/**
 * @author ：xxx
 * @description：TODO 自定义业务异常
 * @date ：2024/02/03 15:10
 */
public class CustomException extends RuntimeException  {
    public CustomException(String message){
        super(message);
    }
}
