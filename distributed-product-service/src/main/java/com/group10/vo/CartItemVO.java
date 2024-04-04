package com.group10.vo;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;

public class CartItemVO {

    /**
     * goods ID
     */
    @JsonProperty("product_id")
    private Long productId;


    /**
     * purchase quantity
     */
    @JsonProperty("buy_num")
    private Integer buyNum;

    /**
     * goods title
     */
    @JsonProperty("product_title")
    private String productTitle;

    /**
     * image
     */
    @JsonProperty("product_img")
    private String productImg;

    /**
     * goods price
     */
    private BigDecimal amount;

    /**
     * Total price, unit price + quantity
     */
    @JsonProperty("total_amount")
    private BigDecimal totalAmount;


    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public Integer getBuyNum() {
        return buyNum;
    }

    public void setBuyNum(Integer buyNum) {
        this.buyNum = buyNum;
    }

    public String getProductTitle() {
        return productTitle;
    }

    public void setProductTitle(String productTitle) {
        this.productTitle = productTitle;
    }

    public String getProductImg() {
        return productImg;
    }

    public void setProductImg(String productImg) {
        this.productImg = productImg;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    /**
     * Unit price * Quantity purchased
     * @return
     */
    public BigDecimal getTotalAmount() {

        return this.amount.multiply(new BigDecimal(this.buyNum));
    }

}
