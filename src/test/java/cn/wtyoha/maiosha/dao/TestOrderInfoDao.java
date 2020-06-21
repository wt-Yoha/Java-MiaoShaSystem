package cn.wtyoha.maiosha.dao;


import cn.wtyoha.miaosha.Application;
import cn.wtyoha.miaosha.dao.GoodsDao;
import cn.wtyoha.miaosha.dao.MiaoShaUserDao;
import cn.wtyoha.miaosha.dao.OrderInfoDao;
import cn.wtyoha.miaosha.domain.Goods;
import cn.wtyoha.miaosha.domain.MiaoShaUser;
import cn.wtyoha.miaosha.domain.OrderInfo;
import cn.wtyoha.miaosha.service.OrderInfoService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class)
public class TestOrderInfoDao {
    @Autowired
    OrderInfoDao orderInfoDao;

    @Autowired
    GoodsDao goodsDao;

    @Autowired
    MiaoShaUserDao miaoShaUserDao;

    @Autowired
    OrderInfoService orderInfoService;

    @Test
    public void testInsert() {
        MiaoShaUser miaoShaUser = miaoShaUserDao.selectByPrimaryKey(15687138742L);
        Goods goods = goodsDao.selectById(11L);
        OrderInfo order = orderInfoService.createOrder(miaoShaUser, goods, 2);
        System.out.println(order);
    }
}
