package com.group10.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.group10.enums.BizCodeEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class JsonData {

    private Integer code;
    private Object data;
    private String msg;


    public <T> T getData(TypeReference<T> typeReference){
        return JSON.parseObject(JSON.toJSONString(data),typeReference);
    }

    public static JsonData buildSuccess() {
        return new JsonData(0, null, null);
    }

    public static JsonData buildSuccess(Object data) {
        return new JsonData(0, data, null);
    }

    public static JsonData buildError(String msg) {
        return new JsonData(-1, null, msg);
    }

    public static JsonData buildCodeAndMsg(int code, String msg) {
        return new JsonData(code, null, msg);
    }

    public static JsonData buildResult(BizCodeEnum codeEnum){
        return JsonData.buildCodeAndMsg(codeEnum.getCode(),codeEnum.getMessage());
    }
}
