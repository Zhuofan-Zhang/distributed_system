package com.group10.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PayInfoVO {

    /**
     * Order No
     */
    private String outTradeNo;

    /**
     * Total Order Amount
     */
    private BigDecimal payFee;

    /**
     * Payment Type WeChat-Alipay-Bank-Other
     */
    private String payType;

    /**
     * End Type APP/H5/PC
     */
    private String clientType;

    /**
     * Title
     */
    private String title;

    /**
     * Description
     */
    private String description;


    /**
     * Order payment timeout, milliseconds
     */
    private long orderPayTimeoutMills;

}
