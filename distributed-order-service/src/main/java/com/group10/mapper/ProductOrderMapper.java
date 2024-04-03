package com.group10.mapper;

import com.group10.model.ProductOrderDO;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

public interface ProductOrderMapper extends BaseMapper<ProductOrderDO> {
    void updateOrderPayState(@Param("outTradeNo") String outTradeNo,@Param("newState") String newState, @Param("oldState") String oldState);

}
