package cn.wtyoha.miaosha.rabbitmq.service.sender;

import cn.wtyoha.miaosha.rabbitmq.config.RabbitMQConfig;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ClearCacheSender {
    @Autowired
    AmqpTemplate amqpTemplate;

    public void sendClearCache(String[] keys) {
        for (String key : keys) {
            amqpTemplate.convertAndSend(RabbitMQConfig.CLEAR_CACHE, key);
        }
    }
    public void sendClearCache(String key) {
        amqpTemplate.convertAndSend(RabbitMQConfig.CLEAR_CACHE, key);
    }
}
