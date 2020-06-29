package cn.wtyoha.miaosha.redis.commonkey;

import cn.wtyoha.miaosha.redis.commonkey.base.BasePrefix;

/**
 * MiaoShaUser模块的通用前缀
 * 完整的key格式为 模块名：自定前缀_key
 * 所以不同模块之间以模块名区分，统一模块之间以自定前缀区分
 */
public class UserKey extends BasePrefix {

    public static UserKey ID = new UserKey("id");
    public static UserKey NAME = new UserKey("name");
    public static UserKey TOKEN = new UserKey("token");

    public UserKey(String prefix) {
        super(prefix);
    }

    public UserKey(int timeout, String prefix) {
        super(timeout, prefix);
    }

}
