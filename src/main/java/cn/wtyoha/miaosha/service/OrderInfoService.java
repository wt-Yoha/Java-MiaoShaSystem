package cn.wtyoha.miaosha.service;

import cn.wtyoha.miaosha.domain.Goods;
import cn.wtyoha.miaosha.domain.MiaoShaUser;
import cn.wtyoha.miaosha.domain.OrderInfo;
import cn.wtyoha.miaosha.rabbitmq.msgdomain.TakeOrder;

import java.util.List;

public interface OrderInfoService {
    TakeOrder takeNormalOder(MiaoShaUser user, Goods goods, int num);

    TakeOrder takeMiaoShaOrder(MiaoShaUser user, Goods goods, int num);

    List<OrderInfo> getUserAllOrders(MiaoShaUser user);

    boolean pay(Long orderId);

    OrderInfo createOrder(MiaoShaUser user, Goods goods, int num);

    OrderInfo selectById(Long id);

    TakeOrder queryTakeOrderStatus(String id);

    boolean undoOrder(MiaoShaUser loginUser, Long orderId);
}
