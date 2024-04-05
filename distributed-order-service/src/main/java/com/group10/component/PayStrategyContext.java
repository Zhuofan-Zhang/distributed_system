package com.group10.component;

import com.group10.vo.PayInfoVO;

public class PayStrategyContext {

    private PayStrategy payStrategy;

    public PayStrategyContext(PayStrategy payStrategy){

        this.payStrategy = payStrategy;
    }


    /**
     * Call different payments according to the payment strategy
     * @param payInfoVO
     * @return
     */
    public String executeUnifiedorder(PayInfoVO payInfoVO){
        return this.payStrategy.unifiedorder(payInfoVO);
    }


    /**
     * Invoke different query order support statuses based on the payment's strategy
     * @param payInfoVO
     * @return
     */
    public String executeQueryPaySuccess(PayInfoVO payInfoVO){
        return this.payStrategy.queryPaySuccess(payInfoVO);

    }


}
