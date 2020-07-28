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
    public void testQueryOne() {
        Goods goods = goodsDao.selectByPrimaryKey(1);
        System.out.println(goods);
    }

    @Test
    public void testUnionQuery(){
        System.out.println(goodsDao.selectById(1L));
    }

    @Test
    public void testUnionQuerySelectAll() {
        System.out.println(goodsDao.selectAll());
    }

    @Test
    public void testSubStock(){
        Long goodsId = 1L;
        Goods goods = goodsDao.selectById(goodsId);
        int affectLine = goodsDao.subStock(goodsId, 1);
        System.out.println(affectLine);
    }

    @Test
    public void testSearchGoods() {
        System.out.println(goodsDao.searchGoods(2, 2, true, "%手表%"));
    }

}
