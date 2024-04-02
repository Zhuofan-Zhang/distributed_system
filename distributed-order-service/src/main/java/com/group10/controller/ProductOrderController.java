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
     * 分页查询我的订单列表
     *
     * @param page
     * @param size
     * @param state
     * @return
     */
    @ApiOperation("分页查询我的订单列表")
    @GetMapping("page")
    public JsonData pagePOrderList(
            @ApiParam(value = "当前页") @RequestParam(value = "page", defaultValue = "1") int page,
            @ApiParam(value = "每页显示多少条") @RequestParam(value = "size", defaultValue = "10") int size,
            @ApiParam(value = "订单状态") @RequestParam(value = "state", required = false) String state
    ) {

        Map<String, Object> pageResult = orderService.page(page, size, state);

        return JsonData.buildSuccess(pageResult);


    }


    /**
     * 查询订单状态
     * <p>
     * 此接口没有登录拦截，可以增加一个秘钥进行rpc通信
     *
     * @param outTradeNo
     * @return
     */
    @ApiOperation("查询订单状态")
    @GetMapping("query_state")
    public JsonData queryProductOrderState(@ApiParam("订单号") @RequestParam("out_trade_no") String outTradeNo) {

        String state = orderService.queryProductOrderState(outTradeNo);

        return StringUtils.isBlank(state) ? JsonData.buildResult(BizCodeEnum.ORDER_CONFIRM_NOT_EXIST) : JsonData.buildSuccess(state);

    }


    @ApiOperation("submit order")
    @PostMapping("confirm")
    public void confirmOrder(@ApiParam("ConfirmOrderRequest") @RequestBody ConfirmOrderRequest orderRequest, HttpServletResponse response) {

        JsonData jsonData = orderService.confirmOrder(orderRequest);

        if (jsonData.getCode() == 0) {
            log.info("创建支付宝订单成功:{}", orderRequest.toString());
            writeData(response, jsonData);
        } else {
            log.error("创建订单失败{}", jsonData.toString());
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
            log.error("写出Html异常：{}", e);
        }

    }


    /**
     * 测试支付方法
     */
    @GetMapping("test_pay")
    public void testAlipay(HttpServletResponse response) throws AlipayApiException, IOException {

        HashMap<String, String> content = new HashMap<>();
        //商户订单号,64个字符以内、可包含字母、数字、下划线；需保证在商户端不重复
        String no = UUID.randomUUID().toString();

        log.info("订单号:{}", no);
        content.put("out_trade_no", no);

        content.put("product_code", "FAST_INSTANT_TRADE_PAY");

        //订单总金额，单位为元，精确到小数点后两位
        content.put("total_amount", String.valueOf("111.99"));

        //商品标题/交易标题/订单标题/订单关键字等。 注意：不可使用特殊字符，如 /，=，&amp; 等。
        content.put("subject", "杯子");

        //商品描述，可空
        content.put("body", "好的杯子");

        // 该笔订单允许的最晚付款时间，逾期将关闭交易。取值范围：1m～15d。m-分钟，h-小时，d-天，1c-当天（1c-当天的情况下，无论交易何时创建，都在0点关闭）。 该参数数值不接受小数点， 如 1.5h，可转换为 90m。
        content.put("timeout_express", "5m");


        AlipayTradeWapPayRequest request = new AlipayTradeWapPayRequest();
        request.setBizContent(JSON.toJSONString(content));
        request.setNotifyUrl(payUrlConfig.getAlipayCallbackUrl());
        request.setReturnUrl(payUrlConfig.getAlipaySuccessReturnUrl());

        AlipayTradeWapPayResponse alipayResponse = AlipayConfig.getInstance().pageExecute(request);

        if (alipayResponse.isSuccess()) {
            System.out.println("调用成功");

            String form = alipayResponse.getBody();

            response.setContentType("text/html;charset=UTF-8");
            response.getWriter().write(form);
            response.getWriter().flush();
            response.getWriter().close();

        } else {
            System.out.println("调用失败");
        }
    }
}

