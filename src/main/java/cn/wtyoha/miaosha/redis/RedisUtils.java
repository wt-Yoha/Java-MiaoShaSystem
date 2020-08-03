package cn.wtyoha.miaosha.redis;

import com.alibaba.fastjson.JSON;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.params.SetParams;

import java.util.List;

@Component
public class RedisUtils {
    public final static int THIRTY_SECONDS = 30;
    public final static int THIRTY_MINUTE = 30 * 60;
    public final static int TWO_MINUTE = 2 * 60;
    public final static String POSITIVE_RES = "OK";

    @Autowired
    JedisPool jedisPool;

    /**
     * 在Redis中存入key-value
     *
     * @param key
     * @param value
     * @param <T>
     * @return bool
     */
    public <T> boolean set(String key, T value) {
        return set(key, value, -1, false);
    }

    public <T> boolean set(String key, T value, int timeout) {
        return set(key, value, timeout, false);
    }

    public <T> boolean set(String key, T value, int timeout, boolean nx) {
        if (key == null || key.length() == 0 || value == null) {
            return false;
        }
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            String sValue = JSON.toJSONString(value);
            SetParams params = new SetParams();
            if (nx) {
                params.nx();
            }
            if (timeout != -1) {
                params.ex(timeout);
            }
            String setResult = jedis.set(key, sValue, params);
            return POSITIVE_RES.equals(setResult);
        } finally {
            close(jedis);
        }
    }

    /**
     * 从redis中获取T类型对象的List集合
     *
     * @param key
     * @param clazz
     * @param <T>
     * @return
     */
    public <T> List<T> getList(String key, Class<T> clazz) {
        if (key == null || key.length() == 0 || clazz == null) {
            return null;
        }
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            String sValue = jedis.get(key);
            return JSON.parseArray(sValue, clazz);
        } finally {
            close(jedis);
        }
    }

    /**
     * 从redis中取出value
     *
     * @param key
     * @param clazz
     * @param <T>
     * @return value
     */
    public <T> T get(String key, Class<T> clazz) {
        if (key == null || key.length() == 0 || clazz == null) {
            return null;
        }
        Jedis jedis = null;
        try {
            if (jedisPool != null) {
                jedis = jedisPool.getResource();
                String sValue = jedis.get(key);
                return JSON.parseObject(sValue, clazz);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            close(jedis);
        }
        return null;
    }

    public boolean exist(String key) {
        if (key == null || key.length() == 0) {
            return false;
        }
        Jedis jedis = null;
        try {
            if (jedisPool != null) {
                jedis = jedisPool.getResource();
                return jedis.exists(key);
            }
        } finally {
            close(jedis);
        }
        return false;
    }

    public Long incr(String key) {
        if (key == null || key.length() == 0) {
            return null;
        }
        Jedis jedis = null;
        try {
            if (jedisPool != null) {
                jedis = jedisPool.getResource();
                return jedis.incr(key);
            }
        } finally {
            close(jedis);
        }
        return null;
    }

    public Long decr(String key) {
        if (key == null || key.length() == 0) {
            return null;
        }
        Jedis jedis = null;
        try {
            if (jedisPool != null) {
                jedis = jedisPool.getResource();
                return jedis.decr(key);
            }
        } finally {
            close(jedis);
        }
        return null;
    }

    private void close(Jedis jedis) {
        if (jedis != null) {
            jedis.close();
        }
    }

    public void deleteKey(String key) {
        if (key == null || key.length() == 0) {
            return;
        }
        Jedis jedis = null;
        try {
            if (jedisPool != null) {
                jedis = jedisPool.getResource();
                jedis.del(key);
            }
        } finally {
            close(jedis);
        }
    }
}
