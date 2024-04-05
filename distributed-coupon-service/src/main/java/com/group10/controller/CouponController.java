package com.group10.controller;


import com.group10.enums.CouponCategoryEnum;
import com.group10.request.NewUserCouponRequest;
import com.group10.service.CouponService;
import com.group10.util.JsonData;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;


@RestController
@RequestMapping("/api/coupon/v1")
public class CouponController {


    @Autowired
    private CouponService couponService;

    @ApiOperation("get coupon list with pagination")
    @GetMapping("page_coupon")
    public JsonData pageCouponList(
            @ApiParam(value = "page_num") @RequestParam(value = "page", defaultValue = "1") int page,
            @ApiParam(value = "page_size") @RequestParam(value = "size", defaultValue = "10") int size
    ) {
        Map<String, Object> pageMap = couponService.getCouponListWithPagination(page, size);
        return JsonData.buildSuccess(pageMap);
    }

    @ApiOperation("add promotional coupon for user")
    @PostMapping("/add/promotion/{coupon_id}")
    public JsonData addPromotionCoupon(@ApiParam(value = "coupon id",required = true) @PathVariable("coupon_id")long couponId){


        return couponService.addCoupon(couponId, CouponCategoryEnum.PROMOTION);
    }


    @ApiOperation("issue new user coupon")
    @PostMapping("/new_user_coupon")
    public JsonData addNewUserCoupon(@ApiParam("NewUserCouponRequest") @RequestBody NewUserCouponRequest newUserCouponRequest ){

        JsonData jsonData = couponService.issueNewUserCoupon(newUserCouponRequest);

        return jsonData;
    }

}

