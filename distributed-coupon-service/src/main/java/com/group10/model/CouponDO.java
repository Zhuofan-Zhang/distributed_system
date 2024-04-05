package com.group10.model;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;


@Data
@EqualsAndHashCode(callSuper = false)
@TableName("coupon")
public class CouponDO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;

    private String category;

    private String publish;

    private String couponImg;

    private String couponTitle;

    private BigDecimal priceDeducted;

    private Integer userLimit;

    private Date validFrom;

    private Date validUntil;

    private Integer inventory;

    private Integer remaining;

    private Date createdAt;

    private BigDecimal conditionPrice;


}
