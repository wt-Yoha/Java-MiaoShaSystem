package cn.wtyoha.maiosha.dao;

import cn.wtyoha.miaosha.Application;
import cn.wtyoha.miaosha.dao.MiaoShaOrderDao;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class)
public class TestMiaoShaOrderDao {
    @Autowired
    MiaoShaOrderDao miaoShaOrderDao;

    @Test
    public void testSelectByUserGoodsId() {
        System.out.println(miaoShaOrderDao.selectByUserGoodsId(1L, 1L));
    }
}
