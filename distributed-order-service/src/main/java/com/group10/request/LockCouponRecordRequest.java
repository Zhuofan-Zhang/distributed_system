package com.group10.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@ApiModel(value = "coupon_targeting",description = "Coupon Targeting")
@Data
public class LockCouponRecordRequest {


    /**
     * Coupon record id list
     */
    @ApiModelProperty(value = "coupon_record_id_list",example = "[1,2,3]")
    private List<Long> lockCouponRecordIds;


    /**
     * Order No.
     */
    @ApiModelProperty(value = "order_no",example = "3234fw234rfd232")
    private String orderOutTradeNo;

}
