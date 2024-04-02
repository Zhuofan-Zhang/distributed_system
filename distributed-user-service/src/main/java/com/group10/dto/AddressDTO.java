package com.group10.dto;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class AddressDTO {
    private Long id;

    private Long userId;

    @JsonProperty("default_status")
    private Integer defaultStatus;

    @JsonProperty("receiver_name")
    private String receiverName;

    private String phone;

    private String state;

    private String city;

    private String district;

    @JsonProperty("detailed_address")
    private String detailedAddress;

}
