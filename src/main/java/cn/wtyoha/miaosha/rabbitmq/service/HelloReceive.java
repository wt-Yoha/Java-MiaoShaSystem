package cn.wtyoha.miaosha.rabbitmq.service;

import cn.wtyoha.miaosha.rabbitmq.config.RabbitMQConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class HelloReceive{
    @Autowired
    AmqpTemplate amqpTemplate;

    @RabbitListener(queues = {RabbitMQConfig.HelloMQ})
    public void processHelloQueue(String msg) {
        processMsg("hello "+msg);
    }

    @RabbitListener(queues = "#{autoDeleteQueue1.name}")
    public void processQueue1(String msg) {
        processMsg("1 "+msg);
    }

    @RabbitListener(queues = "#{autoDeleteQueue2.name}")
    public void processQueue2(String msg) {
        processMsg("2 "+msg);
    }

    private void processMsg(String msg) {
        log.info(msg);
    }
}
