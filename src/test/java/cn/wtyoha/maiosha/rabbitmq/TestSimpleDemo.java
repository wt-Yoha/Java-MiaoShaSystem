package cn.wtyoha.maiosha.rabbitmq;

import cn.wtyoha.miaosha.Application;
import cn.wtyoha.miaosha.rabbitmq.service.HelloSender;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class)
public class TestSimpleDemo {

    @Autowired
    HelloSender helloSender;

    @Test
    public void testDemo() throws InterruptedException {
        for (int i = 0; i < 10; i++) {
            helloSender.send();
            Thread.sleep(1000);
        }
    }

    @Test
    public void testFanoutExchange() throws InterruptedException {
        helloSender.sendToFanoutExchange();
    }
}
