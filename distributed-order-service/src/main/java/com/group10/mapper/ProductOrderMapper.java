package com.group10.mapper;

import com.group10.model.ProductOrderDO;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author zzf
 * @since 2023-09-16
 */
public interface ProductOrderMapper extends BaseMapper<ProductOrderDO> {
    void updateOrderPayState(@Param("outTradeNo") String outTradeNo,@Param("newState") String newState, @Param("oldState") String oldState);

}
