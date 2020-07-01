package cn.wtyoha.miaosha.service.impl;

import cn.wtyoha.miaosha.dao.GoodsDao;
import cn.wtyoha.miaosha.dao.MiaoShaGoodsDao;
import cn.wtyoha.miaosha.dao.MiaoShaOrderDao;
import cn.wtyoha.miaosha.dao.OrderInfoDao;
import cn.wtyoha.miaosha.domain.*;
import cn.wtyoha.miaosha.domain.result.CodeMsg;
import cn.wtyoha.miaosha.globalexception.GlobalException;
import cn.wtyoha.miaosha.rabbitmq.msgdomain.TakeOrder;
import cn.wtyoha.miaosha.rabbitmq.service.sender.ClearCacheSender;
import cn.wtyoha.miaosha.rabbitmq.service.sender.TakeOrderSender;
import cn.wtyoha.miaosha.redis.RedisUtils;
import cn.wtyoha.miaosha.redis.commonkey.OrderKey;
import cn.wtyoha.miaosha.service.OrderInfoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@Slf4j
@Service
@Transactional
public class OderInfoServiceImpl implements OrderInfoService {
    @Autowired
    OrderInfoDao orderInfoDao;

    @Autowired
    MiaoShaOrderDao miaoShaOrderDao;

    @Autowired
    GoodsDao goodsDao;

    @Autowired
    MiaoShaGoodsDao miaoShaGoodsDao;

    @Autowired
    RedisUtils redisUtils;

    @Autowired
    TakeOrderSender takeOrderSender;

    @Autowired
    ClearCacheSender clearCacheSender;

    // 操作库存时的尝试次数
    final int attempts = 10;

    /**
     * 普通商品下订单
     *  @param user
     * @param goods
     * @param num   下单购买数量
     * @return
     */
    @Override
    public TakeOrder takeNormalOder(MiaoShaUser user, Goods goods, int num) {
        OrderInfo order = null;
        // 普通商品,检查库存
        if (goods.getStock() <= 0 || goods.getStock() < num) {
            throw new GlobalException(CodeMsg.PRODUCT_LACK_OF_STOCK);
        }
        // 发送消息
        // 构建消息，同时将消息状态存入redis
        TakeOrder takeOrderMsg = TakeOrder.getInstance(redisUtils, user, goods, num);
        takeOrderSender.sendTakeOrderMsg(takeOrderMsg);
        return takeOrderMsg;
    }

    /**
     * 秒杀商品下单
     *
     * @param user
     * @param goods
     * @param num
     * @return
     */
    @Override
    public OrderInfo takeMiaoShaOrder(MiaoShaUser user, Goods goods, int num) {
        OrderInfo order = null;
        MiaoShaGoods miaoShaGoods = goods.getMiaoShaGoods();
        for (int i = 0; i < attempts; i++) {
            // 查该用户是否已经秒杀过
            MiaoShaOrder miaoShaOrder = miaoShaOrderDao.selectByUserGoodsId(user.getId(), goods.getId());
            if (miaoShaOrder != null) {
                throw new GlobalException(CodeMsg.TOO_LARGE_QUANTITY);
            }
            // 查库存
            if (miaoShaGoods.getStockCount() <= 0 || num > 1) {
                throw new GlobalException(CodeMsg.TOO_LARGE_QUANTITY);
            }
            // cas方式减库存
            int affectLine = miaoShaGoodsDao.subStock(miaoShaGoods.getId(), miaoShaGoods.getStockCount());
            if (affectLine == 0) {
                // 减库存失败，重试
                miaoShaGoods = miaoShaGoodsDao.selectByPrimaryKey(miaoShaGoods.getId());
                log.info("秒杀商品减库存失败：第" + i + "次尝试 " + goods.getId() + " " + goods.getName());
                continue;
            }

            // 加订单
            order = createOrder(user, goods, 1);

            // 加秒杀订单
            miaoShaOrder = new MiaoShaOrder();
            miaoShaOrder.setOrderId(order.getId());
            miaoShaOrder.setUserId(user.getId());
            miaoShaOrder.setGoodsId(goods.getId());
            miaoShaOrderDao.insert(miaoShaOrder);
            return order;
        }
        return order;
    }

    /**
     * 获取用户的所有order
     *
     * @param user
     * @return
     */
    @Override
    public List<OrderInfo> getUserAllOrders(MiaoShaUser user) {
        List<OrderInfo> orderInfoList = redisUtils.getList(OrderKey.USER_ORDERS.getFullKey(user.getId()), OrderInfo.class);
        if (orderInfoList == null) {
            orderInfoList = orderInfoDao.selectByUserId(user.getId());
            redisUtils.set(OrderKey.USER_ORDERS.getFullKey(user.getId()), orderInfoList);
        }
        return orderInfoList;
    }

    /**
     * 支付订单，改变数据库order状态
     *
     * @param orderId
     * @return
     */
    @Override
    public boolean pay(Long orderId) {
        OrderInfo orderInfo = orderInfoDao.selectByPrimaryKey(orderId);
        if (orderInfo.getStatus() == 0) {
            orderInfoDao.setStatus(orderId, 1);
        }
        clearCacheSender.sendClearCache(new String[]{OrderKey.ORDER_ITEM.getFullKey(orderId), OrderKey.USER_ORDERS.getFullKey(orderInfo.getUserId())});
        return true;
    }

    /**
     * 创建OrderInfo
     *
     * @param user
     * @param goods
     * @param num
     * @return
     */
    @Override
    public OrderInfo createOrder(MiaoShaUser user, Goods goods, int num) {
        OrderInfo order = new OrderInfo();
        order.setUserId(user.getId());
        order.setGoodsId(goods.getId());
        order.setGoodsName(goods.getName());
        order.setCreateDate(new Date());
        if (goods.getMiaoShaGoods() != null) {
            order.setGoodsPrice(goods.getMiaoShaGoods().getMiaoshaPrice());
        } else {
            order.setGoodsPrice(goods.getPrice());
        }
        order.setStatus(0);
        order.setGoodsCount(num);
        // 加订单
        orderInfoDao.insert(order);
        return order;
    }

    /**
     * 通过Id查询order（先从缓存中查）
     *
     * @param id
     * @return
     */
    @Override
    public OrderInfo selectById(Long id) {
        OrderInfo orderInfo = redisUtils.get(OrderKey.ORDER_ITEM.getFullKey(id), OrderInfo.class);
        if (orderInfo == null) {
            orderInfo = orderInfoDao.selectByPrimaryKey(id);
            redisUtils.set(OrderKey.ORDER_ITEM.getFullKey(id), orderInfo, RedisUtils.THIRTY_SECONDS);
        }
        return orderInfo;
    }

    @Override
    public TakeOrder queryTakeOrderStatus(String id) {
        String key = OrderKey.ORDER_STATUS.getFullKey(id);
        TakeOrder takeOrder = redisUtils.get(key, TakeOrder.class);
        if (takeOrder == null) {
            throw new GlobalException(CodeMsg.ACCESS_TIME_OUT);
        }
        int status = takeOrder.getStatus();
        if (status == 1 || status == -1) {
            redisUtils.deleteKey(key);
        }
        return takeOrder;
    }
}
