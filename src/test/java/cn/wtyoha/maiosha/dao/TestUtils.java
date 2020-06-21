package cn.wtyoha.maiosha.dao;

import org.junit.Test;

import java.util.Date;

public class TestUtils {

    @Test
    public void testDateApi() {
        long l = System.currentTimeMillis();
        System.out.println(l);
        System.out.println(new Date(l));
        long x = 24*60*60*1000;

        Date y = new Date(x);
        System.out.println(y);
    }
}
