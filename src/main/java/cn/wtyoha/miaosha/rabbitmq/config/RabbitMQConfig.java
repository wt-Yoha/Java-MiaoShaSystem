package cn.wtyoha.miaosha.rabbitmq.config;

import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {
    public static final String FanoutExchange = "FanoutExchange";
    public static final String HelloMQ = "HelloMQ";
    public static final String TAKE_NORMAL_ORDER_QUEUE = "TAKE_NORMAL_ORDER_QUEUE";
    public static final String TAKE_MIAO_SHA_ORDER_QUEUE = "TAKE_MIAO_SHA_ORDER_QUEUE ";
    public static final String CLEAR_CACHE = "CLEAR_CACHE";

    @Bean
    public Queue takeMiaoShaOrderQueue() {
        return new Queue(TAKE_MIAO_SHA_ORDER_QUEUE);
    }

    @Bean
    public Queue clearCache() {
        return new Queue(CLEAR_CACHE);
    }

    @Bean
    public Queue takeNormalOrderQueue() {
        return new Queue(TAKE_NORMAL_ORDER_QUEUE);
    }

    @Bean
    public Queue helloQueue() {
        return new Queue(HelloMQ);
    }

    @Bean
    public Queue autoDeleteQueue1() {
        return new AnonymousQueue();
    }

    @Bean
    public Queue autoDeleteQueue2() {
        return new AnonymousQueue();
    }

    @Bean
    public FanoutExchange fanoutExchange(Queue helloQueue, Queue autoDeleteQueue1, Queue autoDeleteQueue2) {
        return new FanoutExchange(FanoutExchange);
    }

    @Bean
    public Binding bindHello(Queue helloQueue, FanoutExchange exchange) {
        return BindingBuilder.bind(helloQueue).to(exchange);
    }



    @Bean
    public Binding bind1(Queue autoDeleteQueue1, FanoutExchange exchange) {
        return BindingBuilder.bind(autoDeleteQueue1).to(exchange);
    }

    @Bean
    public Binding bind2(Queue autoDeleteQueue2, FanoutExchange exchange) {
        return BindingBuilder.bind(autoDeleteQueue2).to(exchange);
    }
}
