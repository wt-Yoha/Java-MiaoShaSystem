package cn.wtyoha.miaosha.rabbitmq.service.receiver;

import cn.wtyoha.miaosha.dao.GoodsDao;
import cn.wtyoha.miaosha.domain.Goods;
import cn.wtyoha.miaosha.domain.MiaoShaUser;
import cn.wtyoha.miaosha.domain.OrderInfo;
import cn.wtyoha.miaosha.domain.result.CodeMsg;
import cn.wtyoha.miaosha.globalexception.GlobalException;
import cn.wtyoha.miaosha.rabbitmq.config.RabbitMQConfig;
import cn.wtyoha.miaosha.rabbitmq.msgdomain.TakeOrder;
import cn.wtyoha.miaosha.rabbitmq.service.sender.ClearCacheSender;
import cn.wtyoha.miaosha.redis.RedisUtils;
import cn.wtyoha.miaosha.redis.commonkey.GoodsKey;
import cn.wtyoha.miaosha.redis.commonkey.OrderKey;
import cn.wtyoha.miaosha.service.OrderInfoService;
import com.alibaba.fastjson.JSON;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class TakeOrderReceiver {
    @Autowired
    OrderInfoService orderInfoService;

    @Autowired
    GoodsDao goodsDao;

    @Autowired
    RedisUtils redisUtils;

    @Autowired
    ClearCacheSender clearCacheSender;

    /**
     * 从消息队列中获取下单请求
     * @param msg
     * @throws InterruptedException
     */
    @RabbitListener(queues = RabbitMQConfig.TAKE_ORDER_QUEUE)
    public void takeOrder(String msg) throws InterruptedException {
        // 模拟后台处理
        Thread.sleep(5000);
        TakeOrder takeOrder = JSON.parseObject(msg, TakeOrder.class);
        MiaoShaUser user = takeOrder.getUser();
        Goods goods = takeOrder.getGoods();
        int num = takeOrder.getNum();
        // 减库存
        int affectLine = goodsDao.subStock(goods.getId(), num);
        if (affectLine == 0) {
            // 库存不足设置状态
            takeOrder.setStatus(-1, redisUtils);
            throw new GlobalException(CodeMsg.PRODUCT_LACK_OF_STOCK);
        }
        // 创建订单
        OrderInfo order = orderInfoService.createOrder(user, goods, num);
        // 发出清缓存的消息
        clearCacheSender.sendClearCache(GoodsKey.GOODS_ITEM.getFullKey(goods.getId()));
        clearCacheSender.sendClearCache(OrderKey.USER_ORDERS.getFullKey(user.getId()));
        takeOrder.setOrderInfo(order);
        takeOrder.setStatus(1, redisUtils);
    }
}
