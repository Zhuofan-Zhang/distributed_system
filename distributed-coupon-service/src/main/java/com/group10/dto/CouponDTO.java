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
     * coupon category
     */
    private String category;



    /**
     * coupon description
     */
    @JsonProperty("coupon_img")
    private String couponImg;

    /**
     * coupon title
     */
    @JsonProperty("coupon_title")
    private String couponTitle;

    /**
     * coupon price
     */
    @JsonProperty("price_deducted")
    private BigDecimal priceDeducted;

    /**
     * coupon user limit
     */
    @JsonProperty("user_limit")
    private Integer userLimit;

    /**
     * coupon start time
     */
    @JsonFormat(pattern = "yyyy-MM-dd hh:mm:ss",locale = "zh",timezone = "GMT+8")
    @JsonProperty("valid_from")
    private Date validFrom;

    /**
     * coupon end time
     */
    @JsonFormat(pattern = "yyyy-MM-dd hh:mm:ss",locale = "zh",timezone = "GMT+8")
    @JsonProperty("valid_until")
    private Date validUntil;

    /**
     * coupon amount
     */
    @JsonProperty("inventory")
    private Integer inventory;

    /**
     * coupon remaining
     */
    private Integer remaining;



    /**
     * coupon condition
     */
    @JsonProperty("condition_price")
    private BigDecimal conditionPrice;

}
