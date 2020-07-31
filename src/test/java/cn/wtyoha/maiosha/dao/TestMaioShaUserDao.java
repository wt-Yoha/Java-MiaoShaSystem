package cn.wtyoha.maiosha.dao;

import cn.wtyoha.miaosha.Application;
import cn.wtyoha.miaosha.dao.MiaoShaUserDao;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class)
public class TestMaioShaUserDao {

    @Autowired
    MiaoShaUserDao miaoShaUserDao;

    @Test
    public void testInserAdvices() {
        miaoShaUserDao.insertAdvice(15687138742L, "哈哈哈");
    }

}
