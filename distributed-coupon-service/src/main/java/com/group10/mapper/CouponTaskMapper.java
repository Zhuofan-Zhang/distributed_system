package com.group10.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.group10.model.CouponTaskDO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface CouponTaskMapper extends BaseMapper<CouponTaskDO> {

    /**
     * insert batch
     * @param couponTaskDOList
     * @return
     */
    int insertBatch(@Param("couponTaskList") List<CouponTaskDO> couponTaskDOList);
}
