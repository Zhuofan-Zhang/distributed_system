package com.group10.controller;


import com.alibaba.fastjson.JSON;
import com.alipay.api.AlipayApiException;
import com.alipay.api.request.AlipayTradeWapPayRequest;
import com.alipay.api.response.AlipayTradeWapPayResponse;
import com.group10.config.AlipayConfig;
import com.group10.config.PayUrlConfig;
import com.group10.constants.CacheKey;
import com.group10.enums.BizCodeEnum;
import com.group10.interceptor.LoginInterceptor;
import com.group10.model.LoginUser;
import com.group10.request.ConfirmOrderRequest;
import com.group10.service.ProductOrderService;
import com.group10.util.CommonUtil;
import com.group10.util.JsonData;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Api("order module")
@Slf4j
@RestController
@RequestMapping("/api/order/v1")
public class ProductOrderController {


    @Autowired
    private ProductOrderService orderService;

    @Autowired
    private PayUrlConfig payUrlConfig;

    @Autowired
    private StringRedisTemplate redisTemplate;


    @ApiOperation("get token for submitting order")
    @GetMapping("/get_token")
    public JsonData getOrderToken() {
        LoginUser loginUser = LoginInterceptor.threadLocal.get();
        String key = String.format(CacheKey.SUBMIT_ORDER_TOKEN_KEY, loginUser.getId());
        String token = CommonUtil.getStringNumRandom(32);

        redisTemplate.opsForValue().set(key, token, 30, TimeUnit.MINUTES);

        return JsonData.buildSuccess(token);


    }


    /**
     * Paging through my order list
     *
     * @param page
     * @param size
     * @param state
     * @return
     */
    @ApiOperation("Paging through my order list")
    @GetMapping("page")
    public JsonData pagePOrderList(
            @ApiParam(value = "current_page") @RequestParam(value = "page", defaultValue = "1") int page,
            @ApiParam(value = "How_many_entries_per_page") @RequestParam(value = "size", defaultValue = "10") int size,
            @ApiParam(value = "order_state") @RequestParam(value = "state", required = false) String state
    ) {

        Map<String, Object> pageResult = orderService.page(page, size, state);

        return JsonData.buildSuccess(pageResult);


    }


    /**
     * Check Order Status
     * <p>
     * This interface has no login blocking, you can add a secret key for rpc communication
     *
     * @param outTradeNo
     * @return
     */
    @ApiOperation("Check Order Status")
    @GetMapping("query_state")
    public JsonData queryProductOrderState(@ApiParam("order_no") @RequestParam("out_trade_no") String outTradeNo) {

        String state = orderService.queryProductOrderState(outTradeNo);

        return StringUtils.isBlank(state) ? JsonData.buildResult(BizCodeEnum.ORDER_CONFIRM_NOT_EXIST) : JsonData.buildSuccess(state);

    }


    @ApiOperation("submit order")
    @PostMapping("confirm")
    public void confirmOrder(@ApiParam("ConfirmOrderRequest") @RequestBody ConfirmOrderRequest orderRequest, HttpServletResponse response) {

        JsonData jsonData = orderService.confirmOrder(orderRequest);

        if (jsonData.getCode() == 0) {
            log.info("Create Alipay Order Successfully:{}", orderRequest.toString());
            writeData(response, jsonData);
        } else {
            log.error("Failed to create order:{}", jsonData.toString());
            CommonUtil.sendJsonMessage(response, jsonData);

        }
    }


    private void writeData(HttpServletResponse response, JsonData jsonData) {

        try {
            response.setContentType("text/html;charset=UTF8");
            response.getWriter().write(jsonData.getData().toString());
            response.getWriter().flush();
            response.getWriter().close();
        } catch (IOException e) {
            log.error("Write the Html exception:{}", e);
        }

    }


    /**
     * Testing payment methods
     */
    @GetMapping("test_pay")
    public void testAlipay(HttpServletResponse response) throws AlipayApiException, IOException {

        HashMap<String, String> content = new HashMap<>();
        //Merchant order number, 64 characters or less, can contain letters, numbers, underscores; need to ensure that in the merchant side is not repeated
        String no = UUID.randomUUID().toString();

        log.info("Order No:{}", no);
        content.put("out_trade_no", no);

        content.put("product_code", "FAST_INSTANT_TRADE_PAY");

        //Total amount of the order in dollars, accurate to two decimal places
        content.put("total_amount", String.valueOf("111.99"));

        //Product Title/Transaction Title/Order Title/Order Keyword, etc. Note: Special characters such as /, =, &amp; etc. are not allowed.
        content.put("subject", "cup");

        //Description of goods, can be empty
        content.put("body", "good cup");

        // The latest payment time allowed for this order, after which the transaction will be closed. Range of values: 1m to 15d. m-minutes, h-hours, d-days, 1c-day (in case of 1c-day, the transaction is closed at 0:00 regardless of when it was created). The value of this parameter does not accept decimal points, e.g. 1.5h, which can be converted to 90m.
        content.put("timeout_express", "5m");


        AlipayTradeWapPayRequest request = new AlipayTradeWapPayRequest();
        request.setBizContent(JSON.toJSONString(content));
        request.setNotifyUrl(payUrlConfig.getAlipayCallbackUrl());
        request.setReturnUrl(payUrlConfig.getAlipaySuccessReturnUrl());

        AlipayTradeWapPayResponse alipayResponse = AlipayConfig.getInstance().pageExecute(request);

        if (alipayResponse.isSuccess()) {
            System.out.println("The call succeeded.");

            String form = alipayResponse.getBody();

            response.setContentType("text/html;charset=UTF-8");
            response.getWriter().write(form);
            response.getWriter().flush();
            response.getWriter().close();

        } else {
            System.out.println("Failure of call");
        }
    }
}

