package com.group10.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@ApiModel(value = "Coupon locked objects",description = "Coupon locked objects")
@Data
public class LockCouponRecordRequest {


    /**
     * coupon record id list
     */
    @ApiModelProperty(value = "coupon record id list",example = "[1,2,3]")
    private List<Long> lockCouponRecordIds;


    /**
     * order number
     */
    @ApiModelProperty(value = "order number",example = "3234fw234rfd232")
    private String orderOutTradeNo;

}
