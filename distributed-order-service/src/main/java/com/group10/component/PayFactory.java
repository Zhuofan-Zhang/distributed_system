package com.group10.component;

import lombok.extern.slf4j.Slf4j;
import com.group10.enums.ProductOrderPayTypeEnum;
import com.group10.vo.PayInfoVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


@Component
@Slf4j
public class PayFactory {


    @Autowired
    private AlipayStrategy alipayStrategy;

    @Autowired
    private WechatPayStrategy wechatPayStrategy;


    /**
     * 创建支付，简单工程模式
     * @param payInfoVO
     * @return
     */
    public String pay(PayInfoVO payInfoVO){

        String payType = payInfoVO.getPayType();

        if(ProductOrderPayTypeEnum.ALIPAY.name().equalsIgnoreCase(payType)){
            //Alipay payment
            PayStrategyContext payStrategyContext = new PayStrategyContext(alipayStrategy);

            return payStrategyContext.executeUnifiedorder(payInfoVO);

        } else if(ProductOrderPayTypeEnum.WECHAT.name().equalsIgnoreCase(payType)){
            //WeChat Payment Not yet realized
            PayStrategyContext payStrategyContext = new PayStrategyContext(wechatPayStrategy);

            return payStrategyContext.executeUnifiedorder(payInfoVO);
        }


        return "";
    }


    /**
     * Query order payment status
     *
     * Payment success returns non-null, others return null
     *
     * @param payInfoVO
     * @return
     */
    public String queryPaySuccess(PayInfoVO payInfoVO){
        String payType = payInfoVO.getPayType();

        if(ProductOrderPayTypeEnum.ALIPAY.name().equalsIgnoreCase(payType)){
            //Alipay payment
            PayStrategyContext payStrategyContext = new PayStrategyContext(alipayStrategy);

            return payStrategyContext.executeQueryPaySuccess(payInfoVO);

        } else if(ProductOrderPayTypeEnum.WECHAT.name().equalsIgnoreCase(payType)){
            //WeChat Payment Not yet realized
            PayStrategyContext payStrategyContext = new PayStrategyContext(wechatPayStrategy);

            return payStrategyContext.executeQueryPaySuccess(payInfoVO);
        }


        return "";


    }




}
