package com.group10.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.group10.config.RabbitMQConfig;
import com.group10.enums.BizCodeEnum;
import com.group10.enums.ProductOrderStateEnum;
import com.group10.enums.StockTaskStateEnum;
import com.group10.exception.BizException;
import com.group10.feign.ProductOrderFeignSerivce;
import com.group10.mapper.ProductMapper;
import com.group10.mapper.ProductTaskMapper;
import com.group10.model.ProductDO;
import com.group10.model.ProductMessage;
import com.group10.model.ProductTaskDO;
import com.group10.request.LockProductRequest;
import com.group10.request.OrderItemRequest;
import com.group10.service.ProductService;
import com.group10.util.JsonData;
import com.group10.vo.ProductVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;


@Slf4j
@Service
public class ProductServiceImpl implements ProductService {

    @Autowired
    private ProductMapper productMapper;


    @Autowired
    private ProductTaskMapper productTaskMapper;


    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private RabbitMQConfig rabbitMQConfig;


    @Autowired
    private ProductOrderFeignSerivce orderFeignSerivce;

    /**
     * product pagination
     * @param page
     * @param size
     * @return
     */
    @Override
    public Map<String, Object> page(int page, int size) {


        Page<ProductDO> pageInfo = new Page<>(page,size);

        IPage<ProductDO> productDOIPage =  productMapper.selectPage(pageInfo,null);

        Map<String,Object> pageMap = new HashMap<>(3);

        pageMap.put("total_record",productDOIPage.getTotal());
        pageMap.put("total_page",productDOIPage.getPages());
        pageMap.put("current_data",productDOIPage.getRecords().stream().map(obj->beanProcess(obj)).collect(Collectors.toList()));

        return pageMap;
    }


    /**
     * find product details by id
     * @param productId
     * @return
     */
    @Override
    public ProductVO findDetailById(long productId) {

        ProductDO productDO = productMapper.selectById(productId);

        return beanProcess(productDO);

    }

    /**
     * batch query
     * @param productIdList
     * @return
     */
    @Override
    public List<ProductVO> findProductsByIdBatch(List<Long> productIdList) {

        List<ProductDO> productDOList =  productMapper.selectList(new QueryWrapper<ProductDO>().in("id",productIdList));

        List<ProductVO> productVOList = productDOList.stream().map(obj->beanProcess(obj)).collect(Collectors.toList());

        return productVOList;
    }


    /**
     * lock-in inventory
     *
     *1)iterate over the items, locking the quantity of each item
     *2)Every time lock, send a delayed message
     *
     * @param lockProductRequest
     * @return
     */
    @Override
    public JsonData lockProductStock(LockProductRequest lockProductRequest) {

        String outTradeNo = lockProductRequest.getOrderOutTradeNo();
        List<OrderItemRequest> itemList  = lockProductRequest.getOrderItemList();

        List<Long> productIdList = itemList.stream().map(OrderItemRequest::getProductId).collect(Collectors.toList());
        List<ProductVO> productVOList = this.findProductsByIdBatch(productIdList);
        Map<Long,ProductVO> productMap = productVOList.stream().collect(Collectors.toMap(ProductVO::getId,Function.identity()));

        for(OrderItemRequest item:itemList){
            int rows = productMapper.lockProductStock(item.getProductId(),item.getBuyNum());
            if(rows != 1){
                throw new BizException(BizCodeEnum.ORDER_CONFIRM_LOCK_PRODUCT_FAIL);
            }else {
                ProductVO productVO = productMap.get(item.getProductId());
                ProductTaskDO productTaskDO = new ProductTaskDO();
                productTaskDO.setBuyNum(item.getBuyNum());
                productTaskDO.setLockState(StockTaskStateEnum.LOCK.name());
                productTaskDO.setProductId(item.getProductId());
                productTaskDO.setProductName(productVO.getTitle());
                productTaskDO.setOutTradeNo(outTradeNo);
                productTaskMapper.insert(productTaskDO);
                log.info("Item inventory lock - Insert item product_task successfully:{}",productTaskDO);

                ProductMessage productMessage = new ProductMessage();
                productMessage.setOutTradeNo(outTradeNo);
                productMessage.setTaskId(productTaskDO.getId());

                rabbitTemplate.convertAndSend(rabbitMQConfig.getEventExchange(),rabbitMQConfig.getStockReleaseDelayRoutingKey(),productMessage);
                log.info("Goods inventory lock information delay message sent successfully:{}",productMessage);

            }

        }
        return JsonData.buildSuccess();
    }


    /**
     * inventory release
     * @param productMessage
     * @return
     */
    @Override
    public boolean releaseProductStock(ProductMessage productMessage) {

        //example query the work order status
        ProductTaskDO taskDO = productTaskMapper.selectOne(new QueryWrapper<ProductTaskDO>().eq("id",productMessage.getTaskId()));
        if(taskDO == null){
            log.warn("The work order does not exist. The message body is:{}",productMessage);
        }

        //The lock status is processed
        if(taskDO.getLockState().equalsIgnoreCase(StockTaskStateEnum.LOCK.name())){

            //Check order status
            JsonData jsonData = orderFeignSerivce.queryProductOrderState(productMessage.getOutTradeNo());

            if(jsonData.getCode() == 0){

                String state = jsonData.getData().toString();

                if(ProductOrderStateEnum.NEW.name().equalsIgnoreCase(state)){
                    log.warn("The order status is NEW, returned to the message queue, and redelivered:{}",productMessage);
                    return false;
                }

                if(ProductOrderStateEnum.PAY.name().equalsIgnoreCase(state)){
                    taskDO.setLockState(StockTaskStateEnum.FINISH.name());
                    productTaskMapper.update(taskDO,new QueryWrapper<ProductTaskDO>().eq("id",productMessage.getTaskId()));
                    log.info("Order has been paid, modify inventory lock work order FINISH status:{}",productMessage);
                    return true;
                }
            }

            log.warn("The order does not exist, or the order is canceled, confirm the message, change the task status to CANCEL, restore the inventory of the item, message:{}",productMessage);
            taskDO.setLockState(StockTaskStateEnum.CANCEL.name());
            productTaskMapper.update(taskDO,new QueryWrapper<ProductTaskDO>().eq("id",productMessage.getTaskId()));


            productMapper.unlockProductStock(taskDO.getProductId(),taskDO.getBuyNum());

            return true;

        } else {
            log.warn("The work order status is not LOCK,state={},message body={}",taskDO.getLockState(),productMessage);
            return true;
        }

    }


    private ProductVO beanProcess(ProductDO productDO) {

        ProductVO productVO = new ProductVO();
        BeanUtils.copyProperties(productDO,productVO);
        productVO.setStock( productDO.getStock() - productDO.getLockStock());
        return productVO;
    }
}
