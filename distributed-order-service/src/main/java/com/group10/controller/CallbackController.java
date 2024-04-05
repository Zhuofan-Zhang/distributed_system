package com.group10.controller;

import com.alipay.api.AlipayApiException;
import com.alipay.api.internal.util.AlipaySignature;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import com.group10.config.AlipayConfig;
import com.group10.enums.ProductOrderPayTypeEnum;
import com.group10.service.ProductOrderService;
import com.group10.util.JsonData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Api("Order Callback Notification Module")
@Controller
@RequestMapping("/api/callback/order/v1")
@Slf4j
public class CallbackController {


    @Autowired
    private ProductOrderService productOrderService;


    /**
     * Payment callback notification post method
     * @param request
     * @param response
     * @return
     */
    @PostMapping("alipay")
    public String alipayCallback(HttpServletRequest request, HttpServletResponse response){
        //Store all parameters received in an asynchronous notification into map
        Map<String,String> paramsMap = convertRequestParamsToMap(request);
        log.info("Alipay callback notification results:{}",paramsMap);
        //Call the SDK to verify the signature
        try {
            boolean signVerified = AlipaySignature.rsaCheckV1(paramsMap, AlipayConfig.ALIPAY_PUB_KEY, AlipayConfig.CHARSET, AlipayConfig.SIGN_TYPE);
            if(signVerified){

                JsonData jsonData = productOrderService.handlerOrderCallbackMsg(ProductOrderPayTypeEnum.ALIPAY,paramsMap);

                if(jsonData.getCode() == 0){
                    //Notify the result to confirm success, otherwise it will keep on notifying and consider the transaction failed if it doesn't return success eight times
                    return "success";
                }

            }

        } catch (AlipayApiException e) {
            log.info("Paypal callback failed to verify signature:Exception:{}, Parameters:{}",e,paramsMap);
        }

        return "failure";
    }


    /**
     * Convert the parameters in a request into a Map.
     * @param request
     * @return
     */
    private static Map<String, String> convertRequestParamsToMap(HttpServletRequest request) {
        Map<String, String> paramsMap = new HashMap<>(16);
        Set<Map.Entry<String, String[]>> entrySet = request.getParameterMap().entrySet();

        for (Map.Entry<String, String[]> entry : entrySet) {
            String name = entry.getKey();
            String[] values = entry.getValue();
            int size = values.length;
            if (size == 1) {
                paramsMap.put(name, values[0]);
            } else {
                paramsMap.put(name, "");
            }
        }
        return paramsMap;
    }


}
