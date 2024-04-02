package com.group10.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
public class CouponDTO {


    /**
     * id
     */
    private Long id;

    /**
     * 优惠卷类型[NEW_USER注册赠券，TASK任务卷，PROMOTION促销劵]
     */
    private String category;



    /**
     * 优惠券图片
     */
    @JsonProperty("coupon_img")
    private String couponImg;

    /**
     * 优惠券标题
     */
    @JsonProperty("coupon_title")
    private String couponTitle;

    /**
     * 抵扣价格
     */
    @JsonProperty("price_deducted")
    private BigDecimal priceDeducted;

    /**
     * 每人限制张数
     */
    @JsonProperty("user_limit")
    private Integer userLimit;

    /**
     * 优惠券开始有效时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd hh:mm:ss",locale = "zh",timezone = "GMT+8")
    @JsonProperty("valid_from")
    private Date validFrom;

    /**
     * 优惠券失效时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd hh:mm:ss",locale = "zh",timezone = "GMT+8")
    @JsonProperty("valid_until")
    private Date validUntil;

    /**
     * 优惠券总量
     */
    @JsonProperty("inventory")
    private Integer inventory;

    /**
     * 库存
     */
    private Integer remaining;



    /**
     * 满多少才可以使用
     */
    @JsonProperty("condition_price")
    private BigDecimal conditionPrice;

}
