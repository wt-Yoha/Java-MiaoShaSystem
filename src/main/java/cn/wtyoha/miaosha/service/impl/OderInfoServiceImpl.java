package cn.wtyoha.miaosha.service.impl;

import cn.wtyoha.miaosha.dao.GoodsDao;
import cn.wtyoha.miaosha.dao.MiaoShaGoodsDao;
import cn.wtyoha.miaosha.dao.MiaoShaOrderDao;
import cn.wtyoha.miaosha.dao.OrderInfoDao;
import cn.wtyoha.miaosha.domain.*;
import cn.wtyoha.miaosha.domain.result.CodeMsg;
import cn.wtyoha.miaosha.globalexception.GlobalException;
import cn.wtyoha.miaosha.service.OrderInfoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

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

    // 操作库存时的尝试次数
    final int attempts = 10;

    /**
     * 普通商品下订单
     *
     * @param user
     * @param goods
     * @param num   下单购买数量
     */
    @Override
    public OrderInfo takeNormalOder(MiaoShaUser user, Goods goods, int num) {
        OrderInfo order = null;
        for (int i = 0; i < attempts; i++) {
            // 普通商品,检查库存
            if (goods.getStock() <= 0 || goods.getStock() < num) {
                throw new GlobalException(CodeMsg.PRODUCT_LACK_OF_STOCK);
            }
            // 减库存
            int affectLine = goodsDao.subStock(goods.getId(), num, goods.getStock());
            if (affectLine == 0) {
                // 减库存失败 进入重试
                goods = goodsDao.selectById(goods.getId());
                log.info("减库存失败：第" + i + "次尝试 " + goods.getId() + " " + goods.getName());
                continue;
            }

            // 创建订单
            order = createOrder(user, goods, num);
            return order;
        }
        return order;
    }

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
}
