package com.group10.feign;

import com.group10.request.LockCouponRecordRequest;
import com.group10.util.JsonData;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "distributed-coupon-service")
public interface CouponFeignSerivce {

    /**
     * Check the availability of a user's coupons to prevent horizontal permissions
     * @param recordId
     * @return
     */
    @GetMapping("/api/coupon_record/v1/detail/{record_id}")
    JsonData findUserCouponRecordById(@PathVariable("record_id") long recordId);

    /**
     * Locking Coupon Records
     * @param lockCouponRecordRequest
     * @return
     */
    @PostMapping("/api/coupon_record/v1/lock_records")
    JsonData lockCouponRecords(@RequestBody LockCouponRecordRequest lockCouponRecordRequest);
}
