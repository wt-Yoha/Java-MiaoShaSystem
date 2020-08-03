package cn.wtyoha.maiosha.service;

import cn.wtyoha.miaosha.Application;
import cn.wtyoha.miaosha.dao.MiaoShaUserDao;
import cn.wtyoha.miaosha.domain.MiaoShaUser;
import cn.wtyoha.miaosha.redis.RedisUtils;
import cn.wtyoha.miaosha.redis.commonkey.UserKey;
import cn.wtyoha.miaosha.service.MiaoShaUserService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.UUID;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class)
public class TestMiaoShaUserService {
    @Autowired
    RedisUtils redisUtils;

    @Autowired
    MiaoShaUserDao miaoShaUserDao;

    @Autowired
    MiaoShaUserService miaoShaUserService;

    /**
     * 向redis中存入批量token数据，用于压测
     */
    @Test
    public void setTokens2Redis() throws IOException {
        FileOutputStream outputStream = new FileOutputStream("C:\\Users\\lenovo\\Desktop\\Cookies.cofig.txt");
        MiaoShaUser user = miaoShaUserDao.selectByPrimaryKey(15687138742L);
        for (int i = 0; i < 1000; i++) {
            String token = UUID.randomUUID().toString().replace("-", "");
            outputStream.write((token+"\n").getBytes());
            redisUtils.set(UserKey.TOKEN.getFullKey(token), user, 60*60*24);
        }
        outputStream.close();
    }

    @Test
    public void setNxTest() {
        System.out.println(redisUtils.set("abc", 1234));
        System.out.println(redisUtils.set("abc", true, 60));
        System.out.println(redisUtils.set("abc", 9968, 60));
    }
}
