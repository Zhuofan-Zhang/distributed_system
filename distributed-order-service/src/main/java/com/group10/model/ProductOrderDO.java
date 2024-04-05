package com.group10.model;

import java.math.BigDecimal;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import java.util.Date;
import com.baomidou.mybatisplus.annotation.TableId;
import java.io.Serializable;
import lombok.Data;
import lombok.EqualsAndHashCode;


@Data
@EqualsAndHashCode(callSuper = false)
@TableName("product_order")
public class ProductOrderDO implements Serializable {

    private static final long serialVersionUID = 1L;

      @TableId(value = "id", type = IdType.AUTO)
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
     * Nickname
     */
    private String nickname;

    /**
     * Avatar
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


}
