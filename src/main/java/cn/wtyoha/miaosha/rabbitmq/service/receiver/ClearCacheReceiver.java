package cn.wtyoha.miaosha.rabbitmq.service.receiver;

import cn.wtyoha.miaosha.rabbitmq.config.RabbitMQConfig;
import cn.wtyoha.miaosha.redis.RedisUtils;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ClearCacheReceiver {
    @Autowired
    RedisUtils redisUtils;

    @RabbitListener(queues = RabbitMQConfig.CLEAR_CACHE)
    public void clearCache(String key) {
        redisUtils.deleteKey(key);
    }
}
