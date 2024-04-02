package com.group10.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class ConfirmOrderRequest {

    /**
     * Cart use coupon, deduct price when buying certain amount of dollars
     *
     * !If null or less than zero than no coupon
     */
    @JsonProperty("coupon_record_id")
    private Long couponRecordId;


    /**
     * purchased product list
     * only parse ids, purchase amount read from cart
     */
    @JsonProperty("product_ids")
    private List<Long> productIdList;


    /**
     * payment method
     */
    @JsonProperty("pay_type")
    private String payType;


    /**
     * user decive type
     */
    @JsonProperty("client_type")
    private String clientType;


    /**
     * deliver address type
     */
    @JsonProperty("address_id")
    private long addressId;


    /**
     * total amount parsed from fontend, backend need to verify it
     */
    @JsonProperty("total_amount")
    private BigDecimal totalAmount;


    /**
     * actual paid amount
     * totalAmount - couponDeducted
     */
    @JsonProperty("real_pay_amount")
    private BigDecimal realPayAmount;


    /**
     * 防重令牌
     */
    @JsonProperty("token")
    private String token;

}
