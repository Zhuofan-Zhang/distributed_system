package com.group10.vo;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;




@Data
public class ProductOrderAddressVO {


    private Long id;

    /**
     * 用户id
     */
    private Long userId;

    /**
     * 是否默认收货地址：0->否；1->是
     */
    @JsonProperty("default_status")
    private Integer defaultStatus;

    /**
     * 收发货人姓名
     */
    @JsonProperty("receiver_name")
    private String receiverName;

    /**
     * 收货人电话
     */
    private String phone;

    /**
     * 省/直辖市
     */
    private String state;

    /**
     * 市
     */
    private String city;

    /**
     * 区
     */
    private String district;

    /**
     * 详细地址
     */

    @JsonProperty("detailed_address")
    private String detailedAddress;
}
