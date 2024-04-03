package com.group10.controller;


import com.group10.request.CartItemRequest;
import com.group10.service.CartService;
import com.group10.util.JsonData;
import com.group10.vo.CartItemVO;
import com.group10.vo.CartVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Api("cart")
@RestController
@RequestMapping("/api/cart/v1")
public class CartController {


    @Autowired
    private CartService cartService;


    @ApiOperation("add to cart")
    @PostMapping("add")
    public JsonData addToCart(@ApiParam("CartItem") @RequestBody CartItemRequest cartItemRequest){
        cartService.addToCart(cartItemRequest);
        return JsonData.buildSuccess();
    }

    @ApiOperation("clear cart items")
    @DeleteMapping("clear")
    public JsonData clearCart(){
        cartService.clear();
        return JsonData.buildSuccess();
    }

    @ApiOperation("view my cart")
    @GetMapping("cart")
    public JsonData getMyCart(){
        CartVO cartDTO = cartService.getMyCart();
        return JsonData.buildSuccess(cartDTO);
    }

    @ApiOperation("delete cart item")
    @DeleteMapping("/delete/{product_id}")
    public JsonData deleteItem( @ApiParam(value = "product id",required = true)@PathVariable("product_id")long productId ){

        cartService.deleteItem(productId);
        return JsonData.buildSuccess();
    }

    @ApiOperation("change cart item number")
    @PostMapping("change")
    public JsonData changeItemNum( @ApiParam("CartItemRequest") @RequestBody  CartItemRequest cartItemRequest){

        cartService.changeItemNum(cartItemRequest);

        return JsonData.buildSuccess();
    }

    @ApiOperation("获取对应订单的商品信息")
    @PostMapping("confirm_order_cart_items")
    public JsonData confirmOrderCartItems(@ApiParam("商品id列表") @RequestBody List<Long> productIdList){

        List<CartItemVO> cartItemVOList = cartService.confirmOrderCartItems(productIdList);

        return JsonData.buildSuccess(cartItemVOList);

    }


}
