package com.group10.config;

import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import org.springframework.beans.factory.annotation.Value;


public class AlipayConfig {

    /**
     * Alipay Gateway Address  TODO
     */
    public static final  String PAY_GATEWAY="https://openapi.alipaydev.com/gateway.do";


    /**
     * Alipay APPID TODO
     */
    public static final  String APPID="9021000129610980";

    /**
     * application private key TODO
     */
    public static final String APP_PRI_KEY = "MIIEvAIBADANBgkqhkiG9w0BAQEFAASCBKYwggSiAgEAAoIBAQDQpUVZO23FmtMiN4JlP4sN5iWWAEtYLUfSKBIxa771gPNm7EvrnjJD54fS9YR7aD7k3xQa49eelz3Q5xuTMGW4U1AkY1ShImi5vgM2dzVsJAaja0U4Hk89Ij7sZr2H/JnqRi+QafrYLStqodEPlLApDrR9jwZTLW8d6hgN+GpkFD/oIrTDFTt1tSS5/lxoeGb6supQLvjiGL6VGupMg8tvLisMfDl0NdmN1Zwu9LNSOAzRuJ3NCM5aHbKsVxDCCFdhhGofYUWSq3zQ6XNbFGVQc3gQFzMPkW28e0v5zeZyLaPo7kepk4AsxOm5WBiE9NGxJjdG4oopqBLQvMIZvemxAgMBAAECggEAZVNN2WbeCXAGBwD0J11zl11kdXNRI9JhpbLmGvAbfbiQ/DPXLubVvFptgMrQ+umGWbnoVqK9rPv//jquDo4baCe1rOY8TECcoU+g2cuMuybHvocyx20YyDSgYEgI1UicKsTJBpYhRuiwxAkvsSRhZJ8KkktqI+zVoK513hr89/poKGO90cIgoyhGXM0VRfcgmyTFOL7IRpjSCzupEv4ORBZpIofiu74UyhfVrjFSaE6HUah5QkwnHXF/hfPyq1DsCcF+IntZvsPrFEyK8fEATrsqUNixHFQtYJLT+2piJvlgeEgKQ7JQ2DKFO3aDeiAgaGHIiLuxwVDbJ9g2yL3dHQKBgQD82zcsgvbpRl/1CxZkjpen+r+kGJYhYnUJkuTRRrxxoMIYBQpflEu9mXzgeexj1Ix1VZNXGRuRn5fJczmDoHk0dVUrVP/TTppEl/XG925K2pEYDanENQ8c+TIVNM67GbRM1DrRPadrYoW7OzoVZeZGfbXJI2jbDp+mdcz1elerswKBgQDTPVe3JeOZtIy23IZyx37N1G6YFGHz7/Mr6kCNQwNBsNlPpS9zMcx05d8RvXQ3kpNocfTIXZvXhUte7Ybqe023RWw5TAu3kU/CS5FIlPCTc+ba2XhQ8pzxGHseimOUYfHojt6SPwCxO0+mtfDpsNL0DT030ReU7Ilhc16uhjfTCwKBgEr1IXIMkO2ucxvNYaIFs2eUqeMXdsoGyfNAnJx9f7eflGpSdDTJXXqjWaWh/zXI+Kp/5+HWC5or2W9avR4MIGIwtkLWO9CEaK8U7UNCbmu6R3D9++myYPyR0wr+UJqekBiGgbktscffuQ08DYfyYUVcVtt9p8PoneNW4J+U2RCJAoGAAyijqyVx5daBVCqwB/9bshA/Jx8G5/Lqm/mAxZv+7HiMh1hdAUf5WrYlQ8qldj9G3QT3OBfOQMbJfb3nipIVc2wiVikA89nEa49duMwhXNMa3KLr3aoDFsfa73X6Tzm3uFaKlX1DsWLFqiSGWbg/L08TX0ZNJ/jxhPeIrutq4kkCgYBL5/BqmTxxDcAErPAcaenYSeb8uSXrnLcUJkDSjQTDn2EEy/buYbmnltFSspYBZosLqkg/wayzuA6w2mfBe4yeSqIcECVwaKVd7q/AnEWMuX4ZHl+dyk/y8O3Q1kliPZVunuT3YEKT50K4fx6p7DImhTLtxf4mAgbYE3ZWBBotZg==";

    /**
     * Alipay Public Key TODO
     */
    public static final String ALIPAY_PUB_KEY = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEA0KVFWTttxZrTIjeCZT+LDeYllgBLWC1H0igSMWu+9YDzZuxL654yQ+eH0vWEe2g+5N8UGuPXnpc90OcbkzBluFNQJGNUoSJoub4DNnc1bCQGo2tFOB5PPSI+7Ga9h/yZ6kYvkGn62C0raqHRD5SwKQ60fY8GUy1vHeoYDfhqZBQ/6CK0wxU7dbUkuf5caHhm+rLqUC744hi+lRrqTIPLby4rDHw5dDXZjdWcLvSzUjgM0bidzQjOWh2yrFcQwghXYYRqH2FFkqt80OlzWxRlUHN4EBczD5FtvHtL+c3mci2j6O5HqZOALMTpuVgYhPTRsSY3RuKKKagS0LzCGb3psQIDAQAB";


    /**
     * Signature type
     */
    public static final  String SIGN_TYPE="RSA2";


    /**
     * character encoding
     */
    public static final  String CHARSET="UTF-8";


    /**
     * Return parameter format
     */
    public static final  String FORMAT="json";


    /**
     * Constructor privatization
     */
    private AlipayConfig(){

    }


    private volatile static AlipayClient instance = null;


    /**
     * Single instance fetch, double lock checking
     * @return
     */
    public static AlipayClient getInstance(){

        if(instance==null){
            synchronized (AlipayConfig.class){
                if(instance == null){
                    instance = new DefaultAlipayClient(PAY_GATEWAY,APPID,APP_PRI_KEY,FORMAT,CHARSET,ALIPAY_PUB_KEY,SIGN_TYPE);
                }
            }
        }
        return instance;
    }


}
