package com.group10.feign;

import com.group10.request.LockProductRequest;
import com.group10.util.JsonData;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@FeignClient(name = "distributed-product-service")
public interface ProductFeignService {


    /**
     * Get the price of the latest item in the shopping cart (also empties the corresponding cart item)
     * @param productIdList
     * @return
     */
    @PostMapping("/api/cart/v1/confirm_order_cart_items")
    JsonData confirmOrderCartItem(@RequestBody List<Long> productIdList);


    /**
     * Locked merchandise shopping item inventory
     * @param lockProductRequest
     * @return
     */
    @PostMapping("/api/product/v1/lock_products")
    JsonData lockProductStock(@RequestBody LockProductRequest lockProductRequest);
}
