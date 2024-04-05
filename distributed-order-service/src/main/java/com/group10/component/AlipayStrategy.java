package com.group10.component;

import com.alibaba.fastjson.JSON;
import com.alipay.api.AlipayApiException;
import com.alipay.api.request.AlipayTradePagePayRequest;
import com.alipay.api.request.AlipayTradeQueryRequest;
import com.alipay.api.request.AlipayTradeWapPayRequest;
import com.alipay.api.response.AlipayTradePagePayResponse;
import com.alipay.api.response.AlipayTradeQueryResponse;
import com.alipay.api.response.AlipayTradeWapPayResponse;
import lombok.extern.slf4j.Slf4j;
import com.group10.config.AlipayConfig;
import com.group10.config.PayUrlConfig;
import com.group10.enums.BizCodeEnum;
import com.group10.enums.ClientType;
import com.group10.exception.BizException;
import com.group10.vo.PayInfoVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.UUID;

@Slf4j
@Service
public class AlipayStrategy implements PayStrategy {

    @Autowired
    private PayUrlConfig payUrlConfig;


    @Override
    public String unifiedorder(PayInfoVO payInfoVO) {

        HashMap<String,String> content = new HashMap<>();
        //Merchant order number, 64 characters or less, can contain letters, numbers, underscores; need to ensure that in the merchant side is not repeated
        content.put("out_trade_no", payInfoVO.getOutTradeNo());
        content.put("product_code", "FAST_INSTANT_TRADE_PAY");
        //Total amount of the order in dollars, accurate to two decimal places
        content.put("total_amount", payInfoVO.getPayFee().toString());
        //Product Title/Transaction Title/Order Title/Order Keyword, etc. Note: Special characters such as /, =, &amp; etc. are not allowed.
        content.put("subject", payInfoVO.getTitle());
        //Description of goods, can be empty
        content.put("body", payInfoVO.getDescription());

        double timeout = Math.floor(payInfoVO.getOrderPayTimeoutMills()/(1000*60));

        //The front-end also needs to determine whether the order is going to be closed or not, and if it is going to expire soon then no second payment will be made.
        if(timeout<1){ throw  new BizException(BizCodeEnum.PAY_ORDER_PAY_TIMEOUT); }
        // The latest payment time allowed for this order, after which the transaction will be closed. Range of values: 1m to 15d. m-minutes, h-hours, d-days, 1c-day (in case of 1c-day, the transaction is closed at 0:00 regardless of when it was created). The value of this parameter does not accept decimal points, e.g. 1.5h, which can be converted to 90m.
        content.put("timeout_express", Double.valueOf(timeout).intValue()+"m");
        
        String clientType = payInfoVO.getClientType();
        String form = "";

        try{

            if(clientType.equalsIgnoreCase(ClientType.H5.name())){
                //H5 Mobile Web Payment
                AlipayTradeWapPayRequest request = new AlipayTradeWapPayRequest();
                request.setBizContent(JSON.toJSONString(content));
                request.setNotifyUrl(payUrlConfig.getAlipayCallbackUrl());
                request.setReturnUrl(payUrlConfig.getAlipaySuccessReturnUrl());
                AlipayTradeWapPayResponse alipayResponse  = AlipayConfig.getInstance().pageExecute(request);

                log.info("Response Log:alipayResponse={}",alipayResponse);
                if(alipayResponse.isSuccess()){
                    form = alipayResponse.getBody();
                } else {
                    log.error("Paypal build H5 form fails:alipayResponse={},payInfo={}",alipayResponse,payInfoVO);
                }

            }else if(clientType.equalsIgnoreCase(ClientType.PC.name())){
                //PC payment
                AlipayTradePagePayRequest request = new AlipayTradePagePayRequest();
                request.setBizContent(JSON.toJSONString(content));
                request.setNotifyUrl(payUrlConfig.getAlipayCallbackUrl());
                request.setReturnUrl(payUrlConfig.getAlipaySuccessReturnUrl());
                AlipayTradePagePayResponse alipayResponse  = AlipayConfig.getInstance().pageExecute(request);

                log.info("Response Log:alipayResponse={}",alipayResponse);

                if(alipayResponse.isSuccess()){
                    form = alipayResponse.getBody();
                } else {
                    log.error("Paypal build PC form fails:alipayResponse={},payInfo={}",alipayResponse,payInfoVO);
                }

            }

        }catch (AlipayApiException e){
            log.error("Paypal Build Form Exception:payInfo={},异常={}",payInfoVO,e);
        }
        return form;
    }

    @Override
    public String refund(PayInfoVO payInfoVO) {
        return null;
    }


    /**
     * Check order status
     * Payment Successful Returns Non-Null
     * Other returns null
     *
     * Unpaid
     * {"alipay_trade_query_response":{"code": "40004", "msg": "Business Failed", "sub_code": "ACQ.TRADE_NOT_EXIST", "sub_msg": "Transaction not existed", "buyer_ pay_amount": "0.00", "invoice_amount": "0.00", "out_trade_no": "adbe8e8f-3b18-4c9e-b736-02c4c2e15eca", "point_amount": "0.00", "receipt_ amount": "0.00"}, "sign": "xxxxx"}
     *
     * Already paid
     *{"alipay_trade_query_response":{"code": "10000", "msg": "Success", "buyer_logon_id": "mqv***@sandbox.com", "buyer_pay_amount": "0.00","" buyer_user_id": "2088102176996700", "buyer_user_type": "PRIVATE", "invoice_amount": "0.00", "out_trade_no": "adbe8e8f-3b18-4c9e-b736- 02c4c2e15eca", "point_amount": "0.00", "receipt_amount": "0.00", "send_pay_date": "2020-12-04 17:06:47", "total_amount": "111.99", "trade_no ": "2020120422001496700501648498", "trade_status": "TRADE_SUCCESS"}, "sign": "xxxx"}
     *
     * @param payInfoVO
     * @return
     */
    @Override
    public String queryPaySuccess(PayInfoVO payInfoVO) {


        AlipayTradeQueryRequest request = new AlipayTradeQueryRequest();
        HashMap<String,String > content = new HashMap<>();

        //Order Merchant Number, 64-bit
        content.put("out_trade_no",payInfoVO.getOutTradeNo());
        request.setBizContent(JSON.toJSONString(content));

        AlipayTradeQueryResponse response = null;
        try {
            response = AlipayConfig.getInstance().execute(request);
            log.info("Alipay Order Tracking Response：{}",response.getBody());

        } catch (AlipayApiException e) {
            log.error("Alipay order inquiry exception:{}",e);
        }

        if(response.isSuccess()){
            log.info("Alipay Order Status Check Successful:{}",payInfoVO);
            return response.getTradeStatus();
        }else {
            log.info("Failed to check Alipay order status:{}",payInfoVO);
            return "";
        }
    }
}
