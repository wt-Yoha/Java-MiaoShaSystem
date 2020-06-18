package cn.wtyoha.miaosha.redis.commonkey.base;

public class BasePrefix implements KeyPrefix {
    int expireSecons;
    String prefix;

    public BasePrefix(String prefix) {
        expireSecons = 0;
        this.prefix = prefix;
    }

    public BasePrefix(int timeout, String prefix) {
        expireSecons = timeout;
        this.prefix = prefix;
    }

    @Override

    public int expireSeconds() {
        return 0;
    }

    @Override
    public String getPrefix() {
        return this.getClass().getSimpleName() + ":" + prefix;
    }

    public String getFullkey(String key) {
        return getPrefix() + "_" + key;
    }
}
