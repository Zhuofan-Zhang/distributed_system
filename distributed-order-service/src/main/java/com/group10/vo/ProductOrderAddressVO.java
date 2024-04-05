package com.group10.vo;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;




@Data
public class ProductOrderAddressVO {


    private Long id;

    /**
     * User id
     */
    private Long userId;

    /**
     * Default shipping address: 0->No; 1->Yes
     */
    @JsonProperty("default_status")
    private Integer defaultStatus;

    /**
     * Name of consignee or consignor
     */
    @JsonProperty("receiver_name")
    private String receiverName;

    /**
     * Consignee's phone number
     */
    private String phone;

    /**
     * Province/Municipality
     */
    private String state;

    /**
     * City
     */
    private String city;

    /**
     * District
     */
    private String district;

    /**
     * Full address
     */

    @JsonProperty("detailed_address")
    private String detailedAddress;
}
