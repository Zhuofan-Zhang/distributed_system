package com.group10.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.Date;

@Data
@EqualsAndHashCode(callSuper = false)
@TableName("product_task")
public class ProductTaskDO implements Serializable {

    private static final long serialVersionUID = 1L;

      @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     *  Product id
     */
    private Long productId;

    /**
     * Quantity Purchased
     */
    private Integer buyNum;

    /**
     * Product name
     */
    private String productName;

    /**
     * Lock Status Lock LOCK Finish FINISH-Cancel CANCEL
     */
    private String lockState;

    private String outTradeNo;

    private Date createTime;


}
