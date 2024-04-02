package com.group10.exception;

import com.group10.util.JsonData;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

@ControllerAdvice
@Slf4j
public class BizExceptionHandler {

    @ExceptionHandler(value = Exception.class)
    @ResponseBody
    public JsonData  handler(Exception e){
        log.error(e.getMessage());
        if(e instanceof BizException){
            BizException bizException = (BizException) e;
            log.error("Business Exception: {}",e.getMessage());
            return JsonData.buildCodeAndMsg(bizException.getCode(), bizException.getMessage());
        } else {
            log.error(e.getMessage());
            return JsonData.buildError("Global Exception, unknown error.");
        }
    }
}
