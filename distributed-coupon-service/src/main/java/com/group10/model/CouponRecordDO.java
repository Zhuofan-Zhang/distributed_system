package com.group10.model;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

@Data
@EqualsAndHashCode(callSuper = false)
@TableName("coupon_record")
public class CouponRecordDO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;

    private Long couponId;

    private Date createdAt;

    private String usageState;

    private Long userId;

    private String userName;

    private String couponTitle;

    private Date validFrom;

    private Date validUntil;

    private Long orderId;

    private BigDecimal priceDeducted;

    private BigDecimal conditionPrice;


}
