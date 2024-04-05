package com.group10.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.group10.constants.CacheKey;
import com.group10.request.*;
import com.group10.component.PayFactory;
import com.group10.config.RabbitMQConfig;
import com.group10.vo.*;
import com.group10.constants.TimeConstant;
import com.group10.enums.*;
import com.group10.exception.BizException;
import com.group10.feign.CouponFeignSerivce;
import com.group10.feign.ProductFeignService;
import com.group10.feign.UserFeignService;
import com.group10.interceptor.LoginInterceptor;
import com.group10.mapper.ProductOrderItemMapper;
import com.group10.mapper.ProductOrderMapper;
import com.group10.model.LoginUser;
import com.group10.model.OrderMessage;
import com.group10.model.ProductOrderDO;
import com.group10.model.ProductOrderItemDO;
import com.group10.service.ProductOrderService;
import com.group10.util.CommonUtil;
import com.group10.util.JsonData;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;


@Service
@Slf4j
public class ProductOrderServiceImpl implements ProductOrderService {

    @Autowired
    private ProductOrderMapper productOrderMapper;

    @Autowired
    private UserFeignService userFeignService;


    @Autowired
    private ProductFeignService productFeignService;


    @Autowired
    private CouponFeignSerivce couponFeignSerivce;


    @Autowired
    private ProductOrderItemMapper productOrderItemMapper;


    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private RabbitMQConfig rabbitMQConfig;


    @Autowired
    private PayFactory payFactory;


    @Autowired
    private StringRedisTemplate redisTemplate;

    /**
     * * Anti-Resubmission
     * * User Microservices - Confirm shipping address
     * * Commodity Microservices - get latest shopping items and prices
     * * Order price check
     * * Coupon microservice-get coupons
     * * Verify Price
     * * * Lock coupons
     * * Lock item inventory
     * * Create Order Objects
     * * Create child order objects
     * * Send delay messages - for automatic order closure
     * * Create payment messages - for interfacing with third-party payments
     *
     * @param orderRequest
     * @return
     */
    @Override
    @Transactional
    public JsonData confirmOrder(ConfirmOrderRequest orderRequest) {

        LoginUser loginUser = LoginInterceptor.threadLocal.get();

        String orderToken = orderRequest.getToken();
        if (StringUtils.isBlank(orderToken)) {
            throw new BizException(BizCodeEnum.ORDER_CONFIRM_TOKEN_NOT_EXIST);
        }
        //Atomic operations Verify tokens, delete tokens
        String script = "if redis.call('get',KEYS[1]) == ARGV[1] then return redis.call('del',KEYS[1]) else return 0 end";

        Long result = redisTemplate.execute(new DefaultRedisScript<>(script, Long.class), Arrays.asList(String.format(CacheKey.SUBMIT_ORDER_TOKEN_KEY, loginUser.getId())), orderToken);
        if (result == 0L) {
            throw new BizException(BizCodeEnum.ORDER_CONFIRM_TOKEN_EQUAL_FAIL);
        }

        String orderOutTradeNo = CommonUtil.getStringNumRandom(32);


        //Get shipping address details
        ProductOrderAddressVO addressVO = this.getUserAddress(orderRequest.getAddressId());

        log.info("Shipping Address Information:{}", addressVO);

        //Get the items added to the shopping cart by the user
        List<Long> productIdList = orderRequest.getProductIdList();

        JsonData cartItemDate = productFeignService.confirmOrderCartItem(productIdList);
        List<OrderItemVO> orderItemList = cartItemDate.getData(new TypeReference<>() {
        });
        log.info("Acquired commodities:{}", orderItemList);
        if (orderItemList == null) {
            //Shopping cart item does not exist
            throw new BizException(BizCodeEnum.ORDER_CONFIRM_CART_ITEM_NOT_EXIST);
        }

        //Verify price, minus item coupons
        this.checkPrice(orderItemList, orderRequest);
        if (orderRequest.getCouponRecordId() != null) {
            this.lockCouponRecords(orderRequest, orderOutTradeNo);
        }
        //Locked Inventory
        this.lockProductStocks(orderItemList, orderOutTradeNo);


        //Creating Orders
        ProductOrderDO productOrderDO = this.saveProductOrder(orderRequest, loginUser, orderOutTradeNo, addressVO);

        //Creating Order Items
        this.saveProductOrderItems(orderOutTradeNo, productOrderDO.getId(), orderItemList);

        //Send delayed messages for automatic order closure
        OrderMessage orderMessage = new OrderMessage();
        orderMessage.setOutTradeNo(orderOutTradeNo);
        rabbitTemplate.convertAndSend(rabbitMQConfig.getEventExchange(), rabbitMQConfig.getOrderCloseDelayRoutingKey(), orderMessage);


        //Create Payment
        PayInfoVO payInfoVO = new PayInfoVO(orderOutTradeNo,
                productOrderDO.getPayAmount(), orderRequest.getPayType(),
                orderRequest.getClientType(), orderItemList.get(0).getProductTitle(), "", TimeConstant.ORDER_PAY_TIMEOUT_MILLS);

        String payResult = payFactory.pay(payInfoVO);
        if (StringUtils.isNotBlank(payResult)) {
            log.info("Create Payment Order Successfully:payInfoVO={},payResult={}", payInfoVO, payResult);
            return JsonData.buildSuccess(payResult);
        } else {
            log.error("Failed to create payment order:payInfoVO={},payResult={}", payInfoVO, payResult);
            return JsonData.buildResult(BizCodeEnum.PAY_ORDER_FAIL);
        }

    }

    /**
     * New order item
     *
     * @param orderOutTradeNo
     * @param orderId
     * @param orderItemList
     */
    private void saveProductOrderItems(String orderOutTradeNo, Long orderId, List<OrderItemVO> orderItemList) {


        List<ProductOrderItemDO> list = orderItemList.stream().map(
                obj -> {
                    ProductOrderItemDO itemDO = new ProductOrderItemDO();
                    itemDO.setBuyNum(obj.getBuyNum());
                    itemDO.setProductId(obj.getProductId());
                    itemDO.setProductImg(obj.getProductImg());
                    itemDO.setProductName(obj.getProductTitle());

                    itemDO.setOutTradeNo(orderOutTradeNo);
                    itemDO.setCreateTime(new Date());

                    //unit price
                    itemDO.setAmount(obj.getAmount());
                    //total amount
                    itemDO.setTotalAmount(obj.getTotalAmount());
                    itemDO.setProductOrderId(orderId);
                    return itemDO;
                }
        ).collect(Collectors.toList());


        productOrderItemMapper.insertBatch(list);


    }

    /**
     * Creating Orders
     *
     * @param orderRequest
     * @param loginUser
     * @param orderOutTradeNo
     * @param addressVO
     */
    private ProductOrderDO saveProductOrder(ConfirmOrderRequest orderRequest, LoginUser loginUser, String orderOutTradeNo, ProductOrderAddressVO addressVO) {

        ProductOrderDO productOrderDO = new ProductOrderDO();
        productOrderDO.setUserId(loginUser.getId());
        productOrderDO.setHeadImg(loginUser.getAvatar());
        productOrderDO.setNickname(loginUser.getName());

        productOrderDO.setOutTradeNo(orderOutTradeNo);
        productOrderDO.setCreateTime(new Date());
        productOrderDO.setDel(0);
        productOrderDO.setOrderType(ProductOrderTypeEnum.DAILY.name());

        //Actual price paid
        productOrderDO.setPayAmount(orderRequest.getRealPayAmount());

        //Total price, before coupons
        productOrderDO.setTotalAmount(orderRequest.getTotalAmount());
        productOrderDO.setState(ProductOrderStateEnum.NEW.name());
        productOrderDO.setPayType(ProductOrderPayTypeEnum.valueOf(orderRequest.getPayType()).name());

        productOrderDO.setReceiverAddress(JSON.toJSONString(addressVO));

        productOrderMapper.insert(productOrderDO);

        return productOrderDO;

    }

    /**
     * Locked merchandise inventory
     *
     * @param orderItemList
     * @param orderOutTradeNo
     */
    private void lockProductStocks(List<OrderItemVO> orderItemList, String orderOutTradeNo) {

        List<OrderItemRequest> itemRequestList = orderItemList.stream().map(obj -> {

            OrderItemRequest request = new OrderItemRequest();
            request.setBuyNum(obj.getBuyNum());
            request.setProductId(obj.getProductId());
            return request;
        }).collect(Collectors.toList());


        LockProductRequest lockProductRequest = new LockProductRequest();
        lockProductRequest.setOrderOutTradeNo(orderOutTradeNo);
        lockProductRequest.setOrderItemList(itemRequestList);

        JsonData jsonData = productFeignService.lockProductStock(lockProductRequest);
        if (jsonData.getCode() != 0) {
            log.error("Failed to lock item inventory：{}", lockProductRequest);
            throw new BizException(BizCodeEnum.ORDER_CONFIRM_LOCK_PRODUCT_FAIL);
        }
    }

    /**
     * Locked Coupons
     *
     * @param orderRequest
     * @param orderOutTradeNo
     */
    private void lockCouponRecords(ConfirmOrderRequest orderRequest, String orderOutTradeNo) {
        List<Long> lockCouponRecordIds = new ArrayList<>();
        if (orderRequest.getCouponRecordId() > 0) {
            lockCouponRecordIds.add(orderRequest.getCouponRecordId());

            LockCouponRecordRequest lockCouponRecordRequest = new LockCouponRecordRequest();
            lockCouponRecordRequest.setOrderOutTradeNo(orderOutTradeNo);
            lockCouponRecordRequest.setLockCouponRecordIds(lockCouponRecordIds);

            //Initiate a locked coupon request
            JsonData jsonData = couponFeignSerivce.lockCouponRecords(lockCouponRecordRequest);
            if (jsonData.getCode() != 0) {
                throw new BizException(BizCodeEnum.COUPON_RECORD_LOCK_FAIL);
            }
        }

    }

    /**
     * Validating prices
     * 1) Count the price of all products
     * 2) Get the coupon (determine if it meets the conditions of the coupon), the total price minus the price of the coupon is the final price
     * @param orderItem
     * @param orderItemList
     * @param orderRequest
     */
    private void checkPrice(List<OrderItemVO> orderItemList, ConfirmOrderRequest orderRequest) {

        //Statistics on total commodity prices
        BigDecimal realPayAmount = new BigDecimal("0");
        if (orderItemList != null) {
            for (OrderItemVO orderItemVO : orderItemList) {
                BigDecimal itemRealPayAmount = orderItemVO.getTotalAmount();
                realPayAmount = realPayAmount.add(itemRealPayAmount);
            }
        }

        //Get the coupon and determine if it can be used
        CouponRecordVO couponRecordVO = getCartCouponRecord(orderRequest.getCouponRecordId());

        //Calculate the shopping cart price and whether it meets the conditions of the coupon full reduction
        if (couponRecordVO != null) {

            //Calculate whether the full discount is met
            if (realPayAmount.compareTo(couponRecordVO.getConditionPrice()) < 0) {
                throw new BizException(BizCodeEnum.ORDER_CONFIRM_COUPON_FAIL);
            }
            if (couponRecordVO.getPriceDeducted().compareTo(realPayAmount) > 0) {
                realPayAmount = BigDecimal.ZERO;

            } else {
                realPayAmount = realPayAmount.subtract(couponRecordVO.getPriceDeducted());
            }

        }

        if (realPayAmount.compareTo(orderRequest.getRealPayAmount()) != 0) {
            log.error("Order Price Check Failure：{}", orderRequest);
            throw new BizException(BizCodeEnum.ORDER_CONFIRM_PRICE_FAIL);
        }
    }

    /**
     * Get Coupon
     *
     * @param couponRecordId
     * @return
     */
    private CouponRecordVO getCartCouponRecord(Long couponRecordId) {

        if (couponRecordId == null || couponRecordId < 0) {
            return null;
        }

        JsonData couponData = couponFeignSerivce.findUserCouponRecordById(couponRecordId);


        if (couponData.getCode() != 0) {
            throw new BizException(BizCodeEnum.ORDER_CONFIRM_COUPON_FAIL);
        }

        //            CouponRecordVO couponRecordVO = (CouponRecordVO) couponData.getData();
        ObjectMapper mapper = new ObjectMapper();
        CouponRecordVO couponRecordVO = mapper.convertValue(couponData.getData(), CouponRecordVO.class);

        if (!couponAvailable(couponRecordVO)) {
            log.error("Failed to use coupon");
            throw new BizException(BizCodeEnum.COUPON_UNAVAILABLE);
        }
        return couponRecordVO;

    }

    /**
     * Determine if a coupon is available
     *
     * @param couponRecordVO
     * @return
     */
    private boolean couponAvailable(CouponRecordVO couponRecordVO) {

        if (couponRecordVO.getUsageState().equalsIgnoreCase(CouponStateEnum.NEW.name())) {
            long currentTimestamp = CommonUtil.getCurrentTimestamp();
            long end = couponRecordVO.getValidUntil().getTime();
            long start = couponRecordVO.getValidFrom().getTime();
            if (currentTimestamp >= start && currentTimestamp <= end) {
                return true;
            }
        }
        return false;
    }

    /**
     * Get shipping address details
     *
     * @param addressId
     * @return
     */
    private ProductOrderAddressVO getUserAddress(long addressId) {

        JsonData addressData = userFeignService.detail(addressId);

        if (addressData.getCode() != 0) {
            log.error("Failed to get shipping address details,msg:{}", addressData);
            throw new BizException(BizCodeEnum.ADDRESS_NO_EXITS);
        }
        ObjectMapper mapper = new ObjectMapper();

        return mapper.convertValue(addressData.getData(), ProductOrderAddressVO.class);
    }


    /**
     * Check Order Status
     *
     * @param outTradeNo
     * @return
     */
    @Override
    public String queryProductOrderState(String outTradeNo) {
        ProductOrderDO productOrderDO = productOrderMapper.selectOne(new QueryWrapper<ProductOrderDO>().eq("out_trade_no", outTradeNo));

        if (productOrderDO == null) {
            return "";
        } else {
            return productOrderDO.getState();
        }

    }


    /**
     * time-based customs clearance
     *
     * @param orderMessage
     * @return
     */
    @Override
    public boolean closeProductOrder(OrderMessage orderMessage) {

        ProductOrderDO productOrderDO = productOrderMapper.selectOne(new QueryWrapper<ProductOrderDO>().eq("out_trade_no", orderMessage.getOutTradeNo()));

        if (productOrderDO == null) {
            //Order does not exist
            log.warn("Direct confirmation message, order does not exist:{}", orderMessage);
            return true;
        }

        if (productOrderDO.getState().equalsIgnoreCase(ProductOrderStateEnum.PAY.name())) {
            //Paid
            log.info("Direct confirmation message, order paid:{}", orderMessage);
            return true;
        }

        //Check with the third party payment to see if the order is really unpaid
        PayInfoVO payInfoVO = new PayInfoVO();
        payInfoVO.setPayType(productOrderDO.getPayType());
        payInfoVO.setOutTradeNo(orderMessage.getOutTradeNo());
        String payResult = "success";

        //If the result is null, the payment is not successful and the order is canceled locally
        if (StringUtils.isBlank(payResult)) {
            productOrderMapper.updateOrderPayState(productOrderDO.getOutTradeNo(), ProductOrderStateEnum.CANCEL.name(), ProductOrderStateEnum.NEW.name());
            log.info("If the result is null, the payment is not successful and the order is canceled locally:{}", orderMessage);
            return true;
        } else {
            //Payment is successful, the initiative to change the order status to UI on the payment, the cause of the situation may be a problem with the payment channel callbacks
            log.warn("Payment is successful, the initiative to change the order status to UI on the payment, the cause of the situation may be a problem with the payment channel callbacks:{}", orderMessage);
            productOrderMapper.updateOrderPayState(productOrderDO.getOutTradeNo(), ProductOrderStateEnum.PAY.name(), ProductOrderStateEnum.NEW.name());
            return true;
        }


    }

    /***
     * Payment notification results update order status
     * @param payType
     * @param paramsMap
     * @return
     */
    @Override
    public JsonData handlerOrderCallbackMsg(ProductOrderPayTypeEnum payType, Map<String, String> paramsMap) {


        if (payType.name().equalsIgnoreCase(ProductOrderPayTypeEnum.ALIPAY.name())) {
            //Alipay Payment
            //get merchant order number
            String outTradeNo = paramsMap.get("out_trade_no");
            //status of a transaction
            String tradeStatus = paramsMap.get("trade_status");

            if ("TRADE_SUCCESS".equalsIgnoreCase(tradeStatus) || "TRADE_FINISHED".equalsIgnoreCase(tradeStatus)) {
                //Update Order Status
                productOrderMapper.updateOrderPayState(outTradeNo, ProductOrderStateEnum.PAY.name(), ProductOrderStateEnum.NEW.name());
                return JsonData.buildSuccess();
            }

        } else if (payType.name().equalsIgnoreCase(ProductOrderPayTypeEnum.WECHAT.name())) {
        }

        return JsonData.buildResult(BizCodeEnum.PAY_ORDER_CALLBACK_NOT_SUCCESS);
    }

    /**
     * Paging through my orders
     *
     * @param page
     * @param size
     * @param state
     * @return
     */
    @Override
    public Map<String, Object> page(int page, int size, String state) {
        LoginUser loginUser = LoginInterceptor.threadLocal.get();

        Page<ProductOrderDO> pageInfo = new Page<>(page, size);

        IPage<ProductOrderDO> orderDOPage = null;

        if (StringUtils.isBlank(state)) {
            orderDOPage = productOrderMapper.selectPage(pageInfo, new QueryWrapper<ProductOrderDO>().eq("user_id", loginUser.getId()));
        } else {
            orderDOPage = productOrderMapper.selectPage(pageInfo, new QueryWrapper<ProductOrderDO>().eq("user_id", loginUser.getId()).eq("state", state));
        }

        //Get Order List
        List<ProductOrderDO> productOrderDOList = orderDOPage.getRecords();

        List<ProductOrderVO> productOrderVOList = productOrderDOList.stream().map(orderDO -> {

            List<ProductOrderItemDO> itemDOList = productOrderItemMapper.selectList(new QueryWrapper<ProductOrderItemDO>().eq("product_order_id", orderDO.getId()));

            List<OrderItemVO> itemVOList = itemDOList.stream().map(item -> {
                OrderItemVO itemVO = new OrderItemVO();
                BeanUtils.copyProperties(item, itemVO);
                return itemVO;
            }).collect(Collectors.toList());

            ProductOrderVO productOrderVO = new ProductOrderVO();
            BeanUtils.copyProperties(orderDO, productOrderVO);
            productOrderVO.setOrderItemList(itemVOList);
            return productOrderVO;

        }).collect(Collectors.toList());

        Map<String, Object> pageMap = new HashMap<>(3);
        pageMap.put("total_record", orderDOPage.getTotal());
        pageMap.put("total_page", orderDOPage.getPages());
        pageMap.put("current_data", productOrderVOList);

        return pageMap;
    }


    @Override
    @Transactional
    public JsonData repay(RepayOrderRequest repayOrderRequest) {
        LoginUser loginUser = LoginInterceptor.threadLocal.get();

        ProductOrderDO productOrderDO = productOrderMapper.selectOne(new QueryWrapper<ProductOrderDO>().eq("out_trade_no", repayOrderRequest.getOutTradeNo()).eq("user_id", loginUser.getId()));

        log.info("Order Status:{}", productOrderDO);

        if (productOrderDO == null) {
            return JsonData.buildResult(BizCodeEnum.PAY_ORDER_NOT_EXIST);
        }

        //The order status is incorrect, not NEW.
        if (!productOrderDO.getState().equalsIgnoreCase(ProductOrderStateEnum.NEW.name())) {
            return JsonData.buildResult(BizCodeEnum.PAY_ORDER_STATE_ERROR);
        } else {
            //Survival time from order creation to present
            long orderLiveTime = CommonUtil.getCurrentTimestamp() - productOrderDO.getCreateTime().getTime();
            //Creating an order is the tipping point, so add another minute and a few more seconds, and if it's 29 minutes, you won't be able to pay either!
            orderLiveTime = orderLiveTime + 70 * 1000;


            //Greater than the order timeout time, it expires
            if (orderLiveTime > TimeConstant.ORDER_PAY_TIMEOUT_MILLS) {
                return JsonData.buildResult(BizCodeEnum.PAY_ORDER_PAY_TIMEOUT);
            } else {

                //Remember to update the DB order payment parameter payType and also add a log of order payment information TODO


                //Total time - time survived = effective time remaining
                long timeout = TimeConstant.ORDER_PAY_TIMEOUT_MILLS - orderLiveTime;
                //Create Payment
                PayInfoVO payInfoVO = new PayInfoVO(productOrderDO.getOutTradeNo(),
                        productOrderDO.getPayAmount(), repayOrderRequest.getPayType(),
                        repayOrderRequest.getClientType(), productOrderDO.getOutTradeNo(), "", timeout);

                log.info("payInfoVO={}", payInfoVO);
                String payResult = payFactory.pay(payInfoVO);
                if (StringUtils.isNotBlank(payResult)) {
                    log.info("Create secondary payment order successfully:payInfoVO={},payResult={}", payInfoVO, payResult);
                    return JsonData.buildSuccess(payResult);
                } else {
                    log.error("Failed to create secondary payment order:payInfoVO={},payResult={}", payInfoVO, payResult);
                    return JsonData.buildResult(BizCodeEnum.PAY_ORDER_FAIL);
                }

            }


        }


    }
}
