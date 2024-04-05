package com.group10.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;


@ApiModel(value = "commodity lock object",description = "commodity lock object protocol")
@Data
public class LockProductRequest {

    @ApiModelProperty(value = "order id",example = "12312312312")
    @JsonProperty("order_out_trade_no")
    private String orderOutTradeNo;

    @ApiModelProperty(value = "order item")
    @JsonProperty("order_item_list")
    private List<OrderItemRequest> orderItemList;
}
