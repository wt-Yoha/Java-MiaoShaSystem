package cn.wtyoha.miaosha.service;

import cn.wtyoha.miaosha.domain.Goods;
import cn.wtyoha.miaosha.domain.MiaoShaUser;
import cn.wtyoha.miaosha.domain.OrderInfo;

public interface OrderInfoService {
    OrderInfo takeNormalOder(MiaoShaUser user, Goods goods, int num);

    OrderInfo takeMiaoShaOrder(MiaoShaUser user, Goods goods, int num);
}
