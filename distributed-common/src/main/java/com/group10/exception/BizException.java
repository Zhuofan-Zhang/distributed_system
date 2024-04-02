package com.group10.exception;

import com.group10.enums.BizCodeEnum;
import lombok.Getter;

public class BizException extends RuntimeException{
    @Getter
    private int code;
    @Getter
    private String msg;

    public BizException(int code, String msg){
        super(msg);
        this.code = code;
        this.msg = msg;
    }
    public BizException(BizCodeEnum  bizCodeEnum){
        super(bizCodeEnum.getMessage());
        this.code = bizCodeEnum.getCode();
        this.msg = bizCodeEnum.getMessage();
    }
}
