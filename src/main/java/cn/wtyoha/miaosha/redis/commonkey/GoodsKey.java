package cn.wtyoha.miaosha.redis.commonkey;

import cn.wtyoha.miaosha.redis.commonkey.base.BasePrefix;

public class GoodsKey extends BasePrefix {

    public final static GoodsKey GOODS_LIST = new GoodsKey("GOODS_LIST");
    public final static GoodsKey GOODS_ITEM = new GoodsKey("ITEM");
    public final static GoodsKey VALID_MAX_ID = new GoodsKey("VALID_MAX_ID");
    public final static GoodsKey MIAO_SHA_GOODS_STOCK = new GoodsKey("MIAO_SHA_GOODS_STOCK");


    public GoodsKey(String prefix) {
        super(prefix);
    }

    public GoodsKey(int timeout, String prefix) {
        super(timeout, prefix);
    }
}
