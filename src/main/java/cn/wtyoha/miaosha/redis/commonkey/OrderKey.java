package cn.wtyoha.miaosha.redis.commonkey;

import cn.wtyoha.miaosha.redis.commonkey.base.BasePrefix;

public class OrderKey extends BasePrefix {

    public final static OrderKey ORDER_ITEM = new OrderKey("ITEM");
    public final static OrderKey USER_ORDERS = new OrderKey("USER_ORDERS");
    public final static OrderKey ORDER_STATUS = new OrderKey("ORDER_STATUS");
    public final static OrderKey ORDER_PATH = new OrderKey("ORDER_PATH");

    public OrderKey(String prefix) {
        super(prefix);
    }

    public OrderKey(int timeout, String prefix) {
        super(timeout, prefix);
    }
}
