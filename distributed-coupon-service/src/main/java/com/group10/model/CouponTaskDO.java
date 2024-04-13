package com.group10.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.Date;


@Data
@EqualsAndHashCode(callSuper = false)
@TableName("coupon_task")
public class CouponTaskDO implements Serializable {

    private static final long serialVersionUID = 1L;

      @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * coupon record id
     */
    private Long couponRecordId;

    /**
     * coupon creation time
     */
    private Date createTime;

    /**
     * coupon trade number
     */
    private String outTradeNo;

    /**
     * coupon task status
     */
    private String lockState;


}
