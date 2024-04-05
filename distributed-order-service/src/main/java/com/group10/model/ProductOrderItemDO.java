package com.group10.model;

import java.math.BigDecimal;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import java.util.Date;
import com.baomidou.mybatisplus.annotation.TableId;
import java.io.Serializable;
import lombok.Data;
import lombok.EqualsAndHashCode;


@Data
@EqualsAndHashCode(callSuper = false)
@TableName("product_order_item")
public class ProductOrderItemDO implements Serializable {

    private static final long serialVersionUID = 1L;

      @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * Order No.
     */
    private Long productOrderId;

    private String outTradeNo;

    /**
     * Product id
     */
    private Long productId;

    /**
     * Product name
     */
    private String productName;

    /**
     * Product Picture
     */
    private String productImg;

    /**
     * Quantity Purchased
     */
    private Integer buyNum;

    private Date createTime;

    /**
     * Total price of goods purchased
     */
    private BigDecimal totalAmount;

    /**
     * Unit price of purchased items
     */
    private BigDecimal amount;


}
