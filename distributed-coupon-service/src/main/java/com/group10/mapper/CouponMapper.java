package com.group10.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.group10.model.CouponDO;
import org.apache.ibatis.annotations.Param;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author zzf
 * @since 2023-09-09
 */
public interface CouponMapper extends BaseMapper<CouponDO> {

    int reduceStock(@Param("couponId") long couponId);
}
