package com.group10.request;


import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class AddressRequest {

    @ApiModelProperty(value = "default delivery address，0->no；1->yes",example = "0")
    @JsonProperty("default_status")
    private Integer defaultStatus;

    @ApiModelProperty(value = "name",example = "Connor")
    @JsonProperty("receiver_name")
    private String receiverName;

    @ApiModelProperty(value = "phone number",example = "12321312321")
    private String phone;

    @ApiModelProperty(value = "state",example = "Shaanxi")
    private String state;

    @ApiModelProperty(value = "city",example = "Xian")
    private String city;

    @ApiModelProperty(value = "district",example = "Yanta")
    private String district;

    @ApiModelProperty(value = "detailed address",example = "xxxxxxxxxx")
    @JsonProperty("detailed_address")
    private String detailedAddress;
}
