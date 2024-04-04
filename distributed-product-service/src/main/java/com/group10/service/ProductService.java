package com.group10.service;

import com.group10.model.ProductMessage;
import com.group10.request.LockProductRequest;
import com.group10.util.JsonData;
import com.group10.vo.ProductVO;

import java.util.List;
import java.util.Map;

public interface ProductService {

    /**
     * pagination query product list
     * @param page
     * @param size
     * @return
     */
    Map<String,Object> page(int page, int size);

    /**
     * Find product details based on id
     * @param productId
     * @return
     */
    ProductVO findDetailById(long productId);

    /**
     * Batch query products by id
     * @param productIdList
     * @return
     */
    List<ProductVO> findProductsByIdBatch(List<Long> productIdList);

    /**
     * Lock-in inventory
     * @param lockProductRequest
     * @return
     */
    JsonData lockProductStock(LockProductRequest lockProductRequest);

    /**
     * Inventory release
     * @param productMessage
     * @return
     */
    boolean releaseProductStock(ProductMessage productMessage);
}
