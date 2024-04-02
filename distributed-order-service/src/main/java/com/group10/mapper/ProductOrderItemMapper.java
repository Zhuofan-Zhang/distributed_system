package com.group10.mapper;

import com.group10.model.ProductOrderItemDO;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author zzf
 * @since 2023-09-16
 */
public interface ProductOrderItemMapper extends BaseMapper<ProductOrderItemDO> {
    void insertBatch( @Param("orderItemList") List<ProductOrderItemDO> list);

}
