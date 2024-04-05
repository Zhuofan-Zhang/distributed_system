package com.group10.config;

import lombok.Data;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.Exchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@Configuration
@Data
public class RabbitMQConfig {


    /**
     * switch
     */
    @Value("${mqconfig.order_event_exchange}")
    private String eventExchange;


    /**
     * Delay Queue
     */
    @Value("${mqconfig.order_close_delay_queue}")
    private String orderCloseDelayQueue;

    /**
     * customs declaration queue
     */
    @Value("${mqconfig.order_close_queue}")
    private String orderCloseQueue;



    /**
     * Route key to enter the delay queue
     */
    @Value("${mqconfig.order_close_delay_routing_key}")
    private String orderCloseDelayRoutingKey;


    /**
     * Route key for entering the dead letter queue
     */
    @Value("${mqconfig.order_close_routing_key}")
    private String orderCloseRoutingKey;

    /**
     * expiration date
     */
    @Value("${mqconfig.ttl}")
    private Integer ttl;

    /**
     * message converter
     * @return
     */
    @Bean
    public MessageConverter messageConverter(){
        return new Jackson2JsonMessageConverter();
    }


    /**
     * Create switch Topic type, can also use dirct routing
     * Generally one switch for one microservice
     * @return
     */
    @Bean
    public Exchange orderEventExchange(){
        return new TopicExchange(eventExchange,true,false);
    }


    /**
     * Delay Queue
     */
    @Bean
    public Queue orderCloseDelayQueue(){

        Map<String,Object> args = new HashMap<>(3);
        args.put("x-dead-letter-exchange",eventExchange);
        args.put("x-dead-letter-routing-key",orderCloseRoutingKey);
        args.put("x-message-ttl",ttl);

        return new Queue(orderCloseDelayQueue,true,false,false,args);
    }


    /**
     * Dead letter queue, general queue for being listened to
     */
    @Bean
    public Queue orderCloseQueue(){

        return new Queue(orderCloseQueue,true,false,false);

    }


    /**
     * Bindings for the first queue, the delayed queue, are established
     * @return
     */
    @Bean
    public Binding orderCloseDelayBinding(){

        return new Binding(orderCloseDelayQueue,Binding.DestinationType.QUEUE,eventExchange,orderCloseDelayRoutingKey,null);
    }

    /**
     * Dead letter queue binding relationship established
     * @return
     */
    @Bean
    public Binding orderCloseBinding(){

        return new Binding(orderCloseQueue,Binding.DestinationType.QUEUE,eventExchange,orderCloseRoutingKey,null);
    }





}
