package cn.wtyoha.miaosha.rabbitmq.service.sender;

import cn.wtyoha.miaosha.rabbitmq.config.RabbitMQConfig;
import cn.wtyoha.miaosha.rabbitmq.msgdomain.TakeOrder;
import com.alibaba.fastjson.JSON;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class TakeOrderSender {
    @Autowired
    AmqpTemplate amqpTemplate;

    public void sendTakeOrderMsg(TakeOrder msg) {
        amqpTemplate.convertAndSend(RabbitMQConfig.TAKE_ORDER_QUEUE,JSON.toJSONString(msg));
    }
}
