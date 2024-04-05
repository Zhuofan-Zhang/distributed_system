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
     * exchange
     */
    @Value("${mqconfig.stock_event_exchange}")
    private String eventExchange;


    @Value("${mqconfig.stock_release_delay_queue}")
    private String stockReleaseDelayQueue;

    @Value("${mqconfig.stock_release_delay_routing_key}")
    private String stockReleaseDelayRoutingKey;


    @Value("${mqconfig.stock_release_queue}")
    private String stockReleaseQueue;

    @Value("${mqconfig.stock_release_routing_key}")
    private String stockReleaseRoutingKey;

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
     * Create a switch of type Topic, or a dirct route
     * Generally one microservice per switch
     * @return
     */
    @Bean
    public Exchange stockEventExchange(){
        return new TopicExchange(eventExchange,true,false);
    }


    @Bean
    public Queue stockReleaseDelayQueue(){

        Map<String,Object> args = new HashMap<>(3);
        args.put("x-message-ttl",ttl);
        args.put("x-dead-letter-exchange",eventExchange);
        args.put("x-dead-letter-routing-key",stockReleaseRoutingKey);

        return new Queue(stockReleaseDelayQueue,true,false,false,args);
    }


    @Bean
    public Queue stockReleaseQueue(){

        return new Queue(stockReleaseQueue,true,false,false);

    }


    /**
     * The first queue, the delay queue, is bound
     * @return
     */
    @Bean
    public Binding stockReleaseDelayBinding(){

        return new Binding(stockReleaseDelayQueue,Binding.DestinationType.QUEUE,eventExchange,stockReleaseDelayRoutingKey,null);
    }

    /**
     * Dead-letter queue binding relationship establishment
     * @return
     */
    @Bean
    public Binding stockReleaseBinding(){

        return new Binding(stockReleaseQueue,Binding.DestinationType.QUEUE,eventExchange,stockReleaseRoutingKey,null);
    }





}
