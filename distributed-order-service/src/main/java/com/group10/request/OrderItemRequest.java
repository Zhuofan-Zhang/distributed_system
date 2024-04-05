package com.group10.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;


@ApiModel(value = "Commodity sub-items")
@Data
public class OrderItemRequest {


    @ApiModelProperty(value = "Product id",example = "1")
    @JsonProperty("product_id")
    private long productId;

    @ApiModelProperty(value = "Quantity Purchased",example = "2")
    @JsonProperty("buy_num")
    private int buyNum;
}
