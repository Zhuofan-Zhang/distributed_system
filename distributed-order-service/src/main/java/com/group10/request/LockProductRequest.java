package com.group10.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@ApiModel(value = "Commodity Locked Objects",description = "Commodity Locked Object Agreement")
@Data
public class LockProductRequest {

    @ApiModelProperty(value = "order_id",example = "12312312312")
    @JsonProperty("order_out_trade_no")
    private String orderOutTradeNo;

    @ApiModelProperty(value = "order_item")
    @JsonProperty("order_item_list")
    private List<OrderItemRequest> orderItemList;
}
