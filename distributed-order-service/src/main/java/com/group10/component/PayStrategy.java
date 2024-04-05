package com.group10.component;

import com.group10.vo.PayInfoVO;


public interface PayStrategy {


    /**
     * Order
     * @return
     */
    String unifiedorder(PayInfoVO payInfoVO);


    /**
     *  refund
     * @param payInfoVO
     * @return
     */
    default String refund(PayInfoVO payInfoVO){return "";}


    /**
     * Check if the payment is successful
     * @param payInfoVO
     * @return
     */
    default String queryPaySuccess(PayInfoVO payInfoVO){return "";}


}
