package com.group10.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.group10.model.CouponRecordDO;
import org.apache.ibatis.annotations.Param;

import java.util.List;


public interface CouponRecordMapper extends BaseMapper<CouponRecordDO> {

    /**
     * query coupon record by user id
     * @param id
     * @param name
     * @param lockCouponRecordIds
     * @return
     */
    int lockUseStateBatch(@Param("userId") Long userId, @Param("useState") String useState, @Param("lockCouponRecordIds") List<Long> lockCouponRecordIds);

    void updateState(@Param("couponRecordId") Long couponRecordId, @Param("useState") String useState);
}
