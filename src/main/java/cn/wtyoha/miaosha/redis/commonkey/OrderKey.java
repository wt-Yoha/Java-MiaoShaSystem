package cn.wtyoha.miaosha.redis.commonkey;

import cn.wtyoha.miaosha.redis.commonkey.base.BasePrefix;

public class OrderKey extends BasePrefix {

    public final static OrderKey ORDER_ITEM = new OrderKey("ITEM");
    public final static OrderKey USER_ORDERS = new OrderKey("USER_ORDERS");

    public OrderKey(String prefix) {
        super(prefix);
    }

    public OrderKey(int timeout, String prefix) {
        super(timeout, prefix);
    }
}
