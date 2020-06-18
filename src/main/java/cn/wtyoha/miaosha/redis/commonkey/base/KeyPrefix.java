package cn.wtyoha.miaosha.redis.commonkey.base;

public interface KeyPrefix {
    int expireSeconds();
    String getPrefix();
}
