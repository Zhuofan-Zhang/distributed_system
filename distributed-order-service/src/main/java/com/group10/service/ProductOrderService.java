package com.group10.service;

import com.group10.request.ConfirmOrderRequest;
import com.group10.request.RepayOrderRequest;
import com.group10.enums.ProductOrderPayTypeEnum;
import com.group10.model.OrderMessage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.group10.util.JsonData;

import java.util.Map;

/**
 * <p>
 *  Service class
 * </p>
 *
 * @author zzf
 * @since 2023-09-16
 */
public interface ProductOrderService {
    /**
     * Creating Orders
     *
     * @param orderRequest
     * @return
     */
    JsonData confirmOrder(ConfirmOrderRequest orderRequest);

    /**
     * Check Order Status
     *
     * @param outTradeNo
     * @return
     */
    String queryProductOrderState(String outTradeNo);

    /**
     * Queue listening, timed order closure
     *
     * @param orderMessage
     * @return
     */
    boolean closeProductOrder(OrderMessage orderMessage);

    /**
     * Payment Result Callback Notification
     *
     * @param alipay
     * @param paramsMap
     * @return
     */
    JsonData handlerOrderCallbackMsg(ProductOrderPayTypeEnum alipay, Map<String, String> paramsMap);


    /**
     * Paging through my order list
     *
     * @param page
     * @param size
     * @param state
     * @return
     */
    Map<String, Object> page(int page, int size, String state);


    /**
     * Secondary payment for orders
     *
     * @param repayOrderRequest
     * @return
     */
    JsonData repay(RepayOrderRequest repayOrderRequest);
}
