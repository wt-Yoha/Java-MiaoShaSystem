package cn.wtyoha.miaosha.redis;

import com.alibaba.fastjson.JSON;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

@Component
public class RedisUtils {
    @Autowired
    JedisPool jedisPool;

    /**
     * 在Redis中存入key-value
     * @param key
     * @param value
     * @param <T>
     * @return bool
     */
    public <T> boolean set(String key, T value){
        return set(key, value, -1);
    }
    public <T> boolean set(String key, T value, int timeout){
        if (key == null || key.length() == 0 || value == null) {
            return false;
        }
        Jedis jedis = null;
        try {
            if (jedisPool != null) {
                jedis = jedisPool.getResource();
                String sValue = JSON.toJSONString(value);
                if (timeout == -1) {
                    jedis.set(key, sValue);
                } else {
                    jedis.setex(key, timeout, sValue);
                }
                return true;
            }
        }finally {
            close(jedis);
        }
        return false;
    }

    /**
     * 从redis中取出value
     * @param key
     * @param clazz
     * @param <T>
     * @return value
     */
    public <T> T get(String key, Class<T> clazz){
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
        }finally {
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
        } finally{
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
        } finally{
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
        } finally{
            close(jedis);
        }
        return null;
    }

    private void close(Jedis jedis) {
        if (jedis != null) {
            jedis.close();
        }
    }
}
