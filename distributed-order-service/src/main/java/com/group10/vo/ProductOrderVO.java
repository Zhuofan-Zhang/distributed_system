package com.group10.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;



@Data
public class ProductOrderVO {


    private Long id;

    /**
     * Order Unique Identification
     */
    private String outTradeNo;

    /**
     * NEW Unpaid orders, PAY paid orders, CANCEL timeout canceled orders
     */
    private String state;

    /**
     * Order Generation Time
     */
    private Date createTime;

    /**
     * Total Order Amount
     */
    private BigDecimal totalAmount;

    /**
     * Actual price paid for the order
     */
    private BigDecimal payAmount;

    /**
     * Payment type, WeChat - Bank - Alipay
     */
    private String payType;

    /**
     * nicknames
     */
    private String nickname;

    /**
     * avatar
     */
    private String headImg;

    /**
     * User id
     */
    private Long userId;

    /**
     * 0 means not deleted, 1 means deleted
     */
    private Integer del;

    /**
     * Update time
     */
    private Date updateTime;

    /**
     * Order Type DAILY General Order, PROMOTION Promotion Order
     */
    private String orderType;

    /**
     * Shipping address json storage
     */
    private String receiverAddress;


    /**
     * order item
     */
    private List<OrderItemVO> orderItemList;

}
