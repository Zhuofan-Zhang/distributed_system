package com.group10.mq;

import com.group10.service.ProductOrderService;
import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import com.group10.model.OrderMessage;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;


@Slf4j
@Component
@RabbitListener(queues = "${mqconfig.order_close_queue}")
public class ProductOrderMQListener {

    @Autowired
    private ProductOrderService productOrderService;


    /**
     *
     * Consumption of duplicate messages, idempotency guarantees
     * * How to guarantee security in case of concurrency
     *
     * @param orderMessage
     * @param message
     * @param channel
     * @throws IOException
     */
    @RabbitHandler
    public void closeProductOrder(OrderMessage orderMessage, Message message, Channel channel) throws IOException {
        log.info("Listening to messages：closeProductOrder:{}",orderMessage);
        long msgTag = message.getMessageProperties().getDeliveryTag();


        try{

            boolean flag = productOrderService.closeProductOrder(orderMessage);
            if(flag){
                channel.basicAck(msgTag,false);
            }else {
                channel.basicReject(msgTag,true);
            }

        }catch (IOException e){
            log.error("Timed order closure failure:",orderMessage);
            channel.basicReject(msgTag,true);
        }

    }

}
