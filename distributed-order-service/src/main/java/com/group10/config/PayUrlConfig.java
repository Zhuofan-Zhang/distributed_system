package com.group10.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;



@Configuration
@Data
public class PayUrlConfig {

    /**
     * Payment Success Page Jump
     */
    @Value("${alipay.success_return_url}")
    private String alipaySuccessReturnUrl;


    /**
     * Payment success, callback notification
     */
    @Value("${alipay.callback_url}")
    private String alipayCallbackUrl;
}
