package com.group10.service;

import com.group10.enums.CouponCategoryEnum;
import com.group10.request.NewUserCouponRequest;
import com.group10.util.JsonData;

import java.util.Map;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author zzf
 * @since 2023-09-09
 */
public interface CouponService {
    Map<String, Object> getCouponListWithPagination(int page, int size);

    JsonData addCoupon(long couponId, CouponCategoryEnum category);

    JsonData issueNewUserCoupon(NewUserCouponRequest newUserCouponRequest);

}
