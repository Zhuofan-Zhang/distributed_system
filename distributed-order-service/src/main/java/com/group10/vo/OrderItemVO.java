package com.group10.vo;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;

public class OrderItemVO {

    /**
     * Product id
     */
    @JsonProperty("product_id")
    private Long productId;


    /**
     * Quantity Purchased
     */
    @JsonProperty("buy_num")
    private Integer buyNum;

    /**
     * Product title
     */
    @JsonProperty("product_title")
    private String productTitle;

    /**
     * Picture
     */
    @JsonProperty("product_img")
    private String productImg;

    /**
     * Unit price of goods
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
     * Unit price of goods * Quantity purchased
     * @return
     */
    public BigDecimal getTotalAmount() {

        return this.amount.multiply(new BigDecimal(this.buyNum));
    }

    @Override
    public String toString() {
        return "OrderItemVO{" +
                "productId=" + productId +
                ", buyNum=" + buyNum +
                ", productTitle='" + productTitle + '\'' +
                ", productImg='" + productImg + '\'' +
                ", amount=" + amount +
                ", totalAmount=" + totalAmount +
                '}';
    }
}
