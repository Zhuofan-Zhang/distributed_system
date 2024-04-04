package com.group10.vo;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

@Data
@EqualsAndHashCode(callSuper = false)
public class ProductVO  {


    private Long id;

    /**
     * title
     */
    private String title;

    /**
     * surface plot
     */
    @JsonProperty("cover_img")
    private String coverImg;

    /**
     * details
     */
    private String detail;

    /**
     * old price
     */
    @JsonProperty("old_amount")
    private BigDecimal oldAmount;

    /**
     * new price
     */
    private BigDecimal amount;

    /**
     * inventory
     */
    private Integer stock;




}
