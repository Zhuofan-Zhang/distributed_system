package com.group10.mq;

import com.rabbitmq.client.Channel;
import com.group10.model.CouponRecordMessage;
import com.group10.service.CouponRecordService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Slf4j
@Component
@RabbitListener(queues = "${mqconfig.coupon_release_queue}")
public class CouponMQListener {


    @Autowired
    private CouponRecordService couponRecordService;

    /**
     *
     * repeat consumption problem:
     *
     * message consumption is not idempotent, so it is necessary to ensure that the message is consumed only once
     *  if the message is consumed successfully, the message will be confirmed
     *
     *  if the message is not consumed successfully, the message will be rejected
     *
     * @param recordMessage
     * @param message
     * @param channel
     * @throws IOException
     */
    @RabbitHandler
    public void releaseCouponRecord(CouponRecordMessage recordMessage, Message message, Channel channel) throws IOException {

        log.info("Message heard：releaseCouponRecord：{}", recordMessage);
        long msgTag = message.getMessageProperties().getDeliveryTag();

        boolean flag = couponRecordService.releaseCouponRecord(recordMessage);

        try {
            if (flag) {
                //comfirm message
                channel.basicAck(msgTag, false);
            }else {
                log.error("Coupon release failed flag=false,{}",recordMessage);
                channel.basicReject(msgTag,true);
            }

        } catch (IOException e) {
            log.error("Exception in releasing coupon record:{},msg:{}",e,recordMessage);
            channel.basicReject(msgTag,true);
        }

    }

}
