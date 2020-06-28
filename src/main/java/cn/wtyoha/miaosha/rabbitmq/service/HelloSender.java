package cn.wtyoha.miaosha.rabbitmq.service;

import cn.wtyoha.miaosha.rabbitmq.config.RabbitMQConfig;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.core.FanoutExchange;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class HelloSender {
    @Autowired
    AmqpTemplate amqpTemplate;

    @Autowired
    FanoutExchange fanoutExchange;

    public void send() {
        String msg = " MQ " + new Date();
        amqpTemplate.convertAndSend(RabbitMQConfig.HelloMQ, msg);
    }

    public void sendToFanoutExchange() throws InterruptedException {
            String msg = "fanoutExchange ";
            for (int i = 0; i < 5; i++) {
                amqpTemplate.convertAndSend(fanoutExchange.getName(), "", msg + i);
            }
    }
}
