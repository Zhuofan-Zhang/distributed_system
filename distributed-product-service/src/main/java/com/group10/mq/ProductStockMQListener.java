package com.group10.mq;

import com.rabbitmq.client.Channel;
import com.group10.model.ProductMessage;
import com.group10.service.ProductService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;


@Slf4j
@Component
@RabbitListener(queues = "${mqconfig.stock_release_queue}")
public class ProductStockMQListener {


    @Autowired
    private ProductService productService;



    /**
     *
     *
     * @param recordMessage
     * @param message
     * @param channel
     * @throws IOException
     */
    @RabbitHandler
    public void releaseProductStock(ProductMessage productMessage, Message message, Channel channel) throws IOException {

        log.info("Monitored message: releaseProductStock Message content:{}", productMessage);
        long msgTag = message.getMessageProperties().getDeliveryTag();

        boolean flag = productService.releaseProductStock(productMessage);

        try {
            if (flag) {
                channel.basicAck(msgTag, false);
            }else {
                channel.basicReject(msgTag,true);
                log.error("failed to release commodity inventory flag=false,{}",productMessage);
            }

        } catch (IOException e) {
            log.error("release abnormal inventory of goods:{},msg:{}",e,productMessage);
            channel.basicReject(msgTag,true);
        }



    }



}
