package cn.wtyoha.miaosha.redis.commonkey.base;

public class BasePrefix implements KeyPrefix {
    int expireSeconds;
    String prefix;

    public BasePrefix(String prefix) {
        expireSeconds = 0;
        this.prefix = prefix;
    }

    public BasePrefix(int timeout, String prefix) {
        expireSeconds = timeout;
        this.prefix = prefix;
    }

    @Override

    public int expireSeconds() {
        return 0;
    }

    @Override
    public String getPrefix() {
        return this.getClass().getSimpleName() + "." + prefix;
    }

    public String getFullKey() {
        return getPrefix() + "";
    }

    public String getFullKey(Object key) {
        return getPrefix() + "." + key;
    }
}
