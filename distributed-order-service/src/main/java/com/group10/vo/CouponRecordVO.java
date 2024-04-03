package com.group10.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;


@Data
public class CouponRecordVO {


    private Long id;

    @JsonProperty("coupon_id")
    private Long couponId;


    @JsonProperty("usageState")
    private String usageState;

    @JsonProperty("user_id")
    private Long userId;

    @JsonProperty("user_name")
    private String userName;

    @JsonProperty("coupon_title")
    private String couponTitle;

    @JsonFormat(pattern = "yyyy-MM-dd hh:mm:ss",locale = "zh",timezone = "GMT+8")
    @JsonProperty("valid_from")
    private Date validFrom;

    @JsonFormat(pattern = "yyyy-MM-dd hh:mm:ss",locale = "zh",timezone = "GMT+8")
    @JsonProperty("valid_until")
    private Date validUntil;

    @JsonProperty("order_id")
    private Long orderId;

    private BigDecimal priceDeducted;

    @JsonProperty("condition_price")
    private BigDecimal conditionPrice;


}
