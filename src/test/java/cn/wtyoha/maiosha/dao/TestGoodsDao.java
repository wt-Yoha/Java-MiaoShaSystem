package cn.wtyoha.maiosha.dao;

import cn.wtyoha.miaosha.Application;
import cn.wtyoha.miaosha.dao.GoodsDao;
import cn.wtyoha.miaosha.domain.Goods;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class)
public class TestGoodsDao {
    @Autowired
    GoodsDao goodsDao;

    @Test
    public void TestQueryOne() {
        Goods goods = goodsDao.selectByPrimaryKey(1);
        System.out.println(goods);
    }
}
