package com.group10.controller;


import com.group10.request.LockProductRequest;
import com.group10.service.ProductService;
import com.group10.util.JsonData;
import com.group10.vo.ProductVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author zzf
 * @since 2023-09-12
 */
@RestController
@Api("product details service")
@RequestMapping("/api/product/v1")
public class ProductController {

    @Autowired
    private ProductService productService;

    @ApiOperation("get product details with pagination")
    @GetMapping("page")
    public JsonData pageProductList(
            @ApiParam(value = "page")  @RequestParam(value = "page", defaultValue = "1") int page,
            @ApiParam(value = "size") @RequestParam(value = "size", defaultValue = "10") int size
    ){

        Map<String,Object> pageResult = productService.page(page,size);

        return JsonData.buildSuccess(pageResult);
    }

    @ApiOperation("product detail")
    @GetMapping("/detail/{product_id}")
    public JsonData detail(@ApiParam(value = "product_id", required = true) @PathVariable("product_id") long productId) {

        ProductVO productVO = productService.findDetailById(productId);
        return JsonData.buildSuccess(productVO);
    }

    @ApiOperation("lock products")
    @PostMapping("lock_products")
    public JsonData lockProducts(@ApiParam("LockProductRequest") @RequestBody LockProductRequest lockProductRequest) {
        return productService.lockProductStock(lockProductRequest);
    }


}

