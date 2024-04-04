package com.group10.vo;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;
import java.util.List;

public class CartVO {

    /**
     * purchased items
     */
    @JsonProperty("cart_items")
    private List<CartItemVO> cartItems;


    /**
     * total of purchases
     */
    @JsonProperty("total_num")
    private Integer totalNum;

    /**
     * total price of cart
     */
    @JsonProperty("total_amount")
    private BigDecimal totalAmount;

    /**
     * actual payment price of cart
     */
    @JsonProperty("real_pay_amount")
    private BigDecimal realPayAmount;


    /**
     * total number of packages
     * @return
     */
    public Integer getTotalNum() {
        if(this.cartItems!=null){
            int total = cartItems.stream().mapToInt(CartItemVO::getBuyNum).sum();
            return total;
        }
        return 0;
    }

    /**
     * total price
     * @return
     */
    public BigDecimal getTotalAmount() {
        BigDecimal amount = new BigDecimal("0");
        if(this.cartItems!=null){
            for(CartItemVO cartItemVO : cartItems){
                BigDecimal itemTotalAmount =  cartItemVO.getTotalAmount();
                amount = amount.add(itemTotalAmount);
            }
        }
        return amount;
    }

    /**
     * actual price paid in the cart
     * @return
     */
    public BigDecimal getRealPayAmount() {
        BigDecimal amount = new BigDecimal("0");
        if(this.cartItems!=null){
            for(CartItemVO cartItemVO : cartItems){
                BigDecimal itemTotalAmount =  cartItemVO.getTotalAmount();
                amount = amount.add(itemTotalAmount);
            }
        }
        return amount;
    }

    public List<CartItemVO> getCartItems() {
        return cartItems;
    }

    public void setCartItems(List<CartItemVO> cartItems) {
        this.cartItems = cartItems;
    }
}
