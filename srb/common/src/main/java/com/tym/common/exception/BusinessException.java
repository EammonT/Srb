package com.tym.common.exception;

import com.tym.common.result.ResponseEnum;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class BusinessException extends RuntimeException{

    private Integer code;
    private String message;

    public BusinessException(String message){
        this.message = message;
    }

    public BusinessException(String message,Integer code){
        this.message = message;
        this.code = code;
    }

    public BusinessException(String message,Integer code,Throwable cause){
        super(cause);
        this.message = message;
        this.code = code;
    }

    public BusinessException(ResponseEnum responseEnum){
        this.message = responseEnum.getMessage();
        this.code = responseEnum.getCode();
    }

    public BusinessException(ResponseEnum responseEnum,Throwable cause){
        super(cause);
        this.message = responseEnum.getMessage();
        this.code = responseEnum.getCode();
    }
}
