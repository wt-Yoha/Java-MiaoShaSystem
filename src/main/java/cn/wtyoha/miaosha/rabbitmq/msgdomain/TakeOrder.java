package cn.wtyoha.miaosha.rabbitmq.msgdomain;

import cn.wtyoha.miaosha.domain.Goods;
import cn.wtyoha.miaosha.domain.MiaoShaUser;
import cn.wtyoha.miaosha.domain.OrderInfo;
import cn.wtyoha.miaosha.redis.RedisUtils;
import cn.wtyoha.miaosha.redis.commonkey.OrderKey;
import lombok.Data;

import java.util.UUID;

@Data
public class TakeOrder {

    String id;
    MiaoShaUser user;
    Goods goods;
    OrderInfo orderInfo;
    Integer num;
    Integer status = 0; // 表明处理中的订单状态 0 排队处理中、1 下单成功、-1 下单失败


    private TakeOrder() {
    }

    public static TakeOrder getInstance(RedisUtils redisUtils, MiaoShaUser user, Goods goods, Integer num) {
        TakeOrder takeOrder = new TakeOrder();
        takeOrder.setGoods(goods);
        takeOrder.setUser(user);
        takeOrder.setNum(num);
        String id = UUID.randomUUID().toString().replace("-", "");
        takeOrder.setId(id);
        takeOrder.setStatus(0, redisUtils);
        return takeOrder;
    }

    public void setStatus(Integer i, RedisUtils redisUtils) {
        this.status = i;
        redisUtils.set(OrderKey.ORDER_STATUS.getFullKey(id), this, RedisUtils.TWO_MINUTE);
    }
}
