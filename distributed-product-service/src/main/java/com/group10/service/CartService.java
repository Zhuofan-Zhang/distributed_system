package com.group10.service;

import com.group10.request.CartItemRequest;
import com.group10.vo.CartItemVO;
import com.group10.vo.CartVO;

import java.util.List;

public interface CartService {

    /**
     * Add items to cart
     * @param cartItemRequest
     */
    void addToCart(CartItemRequest cartItemRequest);

    /**
     * empty cart
     */
    void clear();

    /**
     * view my cart
     * @return
     */
    CartVO getMyCart();

    /**
     * delete items
     * @param productId
     */
    void deleteItem(long productId);

    /**
     * modify the quantity of shopping cart items
     * @param cartItemRequest
     */
    void changeItemNum(CartItemRequest cartItemRequest);

    List<CartItemVO> confirmOrderCartItems(List<Long> productIdList);
}
