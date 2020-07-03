package cn.wtyoha.miaosha.rabbitmq.service.receiver;

import cn.wtyoha.miaosha.dao.GoodsDao;
import cn.wtyoha.miaosha.domain.*;
import cn.wtyoha.miaosha.domain.result.CodeMsg;
import cn.wtyoha.miaosha.rabbitmq.config.RabbitMQConfig;
import cn.wtyoha.miaosha.rabbitmq.msgdomain.TakeOrder;
import cn.wtyoha.miaosha.rabbitmq.service.sender.ClearCacheSender;
import cn.wtyoha.miaosha.redis.RedisUtils;
import cn.wtyoha.miaosha.redis.commonkey.GoodsKey;
import cn.wtyoha.miaosha.redis.commonkey.OrderKey;
import cn.wtyoha.miaosha.service.MiaoShaGoodsService;
import cn.wtyoha.miaosha.service.MiaoShaOrderService;
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

    @Autowired
    MiaoShaOrderService miaoShaOrderService;

    @Autowired
    MiaoShaGoodsService miaoShaGoodsService;

    /**
     * 从消息队列中获取下单请求
     *
     * @param msg
     * @throws InterruptedException
     */
    @RabbitListener(queues = RabbitMQConfig.TAKE_NORMAL_ORDER_QUEUE)
    public void takeNormalOrder(String msg) throws InterruptedException {
        // 模拟后台处理
        Thread.sleep(3000);
        TakeOrder takeOrder = JSON.parseObject(msg, TakeOrder.class);
        MiaoShaUser user = takeOrder.getUser();
        Goods goods = takeOrder.getGoods();
        int num = takeOrder.getNum();
        // 减库存
        int affectLine = goodsDao.subStock(goods.getId(), num);
        if (affectLine == 0) {
            // 库存不足设置状态
            takeOrder.setErrorMsg(CodeMsg.PRODUCT_LACK_OF_STOCK, redisUtils);
//            takeOrder本身就是存储下单状态的，在RabbitListener中直接抛出异常会导致RabbiMq服务器无法收到ack确认
//            因此直接在takeOrder中存储异常信息即可
//            throw new GlobalException(CodeMsg.PRODUCT_LACK_OF_STOCK);
            return;
        }
        // 创建订单
        OrderInfo order = orderInfoService.createOrder(user, goods, num);
        // 发出清缓存的消息
        clearCacheSender.sendClearCache(GoodsKey.GOODS_ITEM.getFullKey(goods.getId()));
        clearCacheSender.sendClearCache(OrderKey.USER_ORDERS.getFullKey(user.getId()));
        takeOrder.setOrderInfo(order);
        takeOrder.setStatus(1, redisUtils);
    }

    @RabbitListener(queues = RabbitMQConfig.TAKE_MIAO_SHA_ORDER_QUEUE)
    public void takeMiaoShaOrder(String msg) throws InterruptedException {
        Thread.sleep(3000);
        TakeOrder takeOrder = JSON.parseObject(msg, TakeOrder.class);
        MiaoShaUser user = takeOrder.getUser();
        Goods goods = takeOrder.getGoods();
        MiaoShaGoods miaoShaGoods = goods.getMiaoShaGoods();
        Integer num = takeOrder.getNum();

        // 查该用户是否已经秒杀过
        MiaoShaOrder miaoShaOrder = miaoShaOrderService.queryByUserIdAndGoodsId(user.getId(), goods.getId());
        if (miaoShaOrder != null) {
            takeOrder.setErrorMsg(CodeMsg.TOO_LARGE_QUANTITY, redisUtils);
            return;
        }

        // 检查秒杀数量
        if (num > 1) {
            takeOrder.setErrorMsg(CodeMsg.TOO_LARGE_QUANTITY, redisUtils);
            return;
        }

        // 查库存
        if (goods.getMiaoShaGoods().getStockCount() <= 0) {
            takeOrder.setErrorMsg(CodeMsg.PRODUCT_LACK_OF_STOCK, redisUtils);
            return;
        }

        // 减库存
        int affectLine = miaoShaGoodsService.subMiaoShaGoodsStock(miaoShaGoods.getId());
        if (affectLine == 0) {
            takeOrder.setErrorMsg(CodeMsg.TOO_LARGE_QUANTITY, redisUtils);
            return;
        }

        // 加订单
        OrderInfo order = orderInfoService.createOrder(user, goods, 1);

        // 加秒杀订单
        miaoShaOrder = new MiaoShaOrder();
        miaoShaOrder.setOrderId(order.getId());
        miaoShaOrder.setUserId(user.getId());
        miaoShaOrder.setGoodsId(goods.getId());
        miaoShaOrderService.insertMiaoShaOrder(miaoShaOrder);

        // 清楚用户order的缓存
        clearCacheSender.sendClearCache(GoodsKey.GOODS_ITEM.getFullKey(goods.getId()));
        clearCacheSender.sendClearCache(OrderKey.USER_ORDERS.getFullKey(user.getId()));
        takeOrder.setOrderInfo(order);
        takeOrder.setStatus(1, redisUtils);
    }
}
