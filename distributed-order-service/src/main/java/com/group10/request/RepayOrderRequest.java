package com.group10.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;


@Data
public class RepayOrderRequest {


    /**
     * Order No.
     */
    @JsonProperty("out_trade_no")
    private String outTradeNo;



    /**
     * Payment Type - WeChat - Bank Card - Alipay
     */
    @JsonProperty("pay_type")
    private String payType;



    /**
     * Order No.
     */
    @JsonProperty("client_type")
    private String clientType;
}
