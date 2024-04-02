package com.group10.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.group10.model.CouponRecordDO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author zzf
 * @since 2023-09-09
 */
public interface CouponRecordMapper extends BaseMapper<CouponRecordDO> {

    /**
     * 批量更新优惠券使用记录
     * @param id
     * @param name
     * @param lockCouponRecordIds
     * @return
     */
    int lockUseStateBatch(@Param("userId") Long userId, @Param("useState") String useState, @Param("lockCouponRecordIds") List<Long> lockCouponRecordIds);

    void updateState(@Param("couponRecordId") Long couponRecordId, @Param("useState") String useState);
}
