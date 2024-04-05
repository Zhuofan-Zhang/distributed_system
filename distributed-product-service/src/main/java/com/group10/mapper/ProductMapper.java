package com.group10.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.group10.model.ProductDO;
import org.apache.ibatis.annotations.Param;


public interface ProductMapper extends BaseMapper<ProductDO> {

    int lockProductStock(@Param("productId") long productId, @Param("buyNum") int buyNum);

    void unlockProductStock(@Param("productId")Long productId, @Param("buyNum")Integer buyNum);

}
