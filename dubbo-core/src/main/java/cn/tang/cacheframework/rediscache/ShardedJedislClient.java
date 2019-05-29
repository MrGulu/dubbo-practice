package cn.tang.cacheframework.rediscache;

import cn.tang.cacheframework.cache.ICacheClient;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.ShardedJedis;
import redis.clients.util.SafeEncoder;

import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

/**
 * @author tangwenlong
 * @description: ShardedJedislClient
 * @date 2018/7/10 13:39
 */
public class ShardedJedislClient implements ICacheClient {
    protected RedisFactory redisFactory = RedisFactory.getInstance();

    @Override
    public boolean clear(String group) {
        return refresh(group);
    }

    public boolean refresh(String group) {
        ShardedJedis shardedJedis = this.redisFactory.getJedisCache(group);
        if (shardedJedis == null) {
            return false;
        }
        boolean result = false;
        try {
            Collection<Jedis> jedisList = shardedJedis.getAllShards();
            for (Jedis jedis : jedisList) {
                jedis.flushAll();
            }
            result = true;
            this.redisFactory.returnResource(group, shardedJedis);
        } catch (Exception e) {
            this.redisFactory.returnBrokenResource(group, shardedJedis);
        }
        return result;
    }

    @Override
    public Set<String> keySet(String group) {
        ShardedJedis shardedJedis = this.redisFactory.getJedisCache(group);
        if (shardedJedis == null) {
            return null;
        }
        Set result = new HashSet();
        try {
            Collection<Jedis> jedisList = shardedJedis.getAllShards();

            for (Jedis jedis : jedisList) {
                result.addAll(jedis.keys("*"));
            }

            this.redisFactory.returnResource(group, shardedJedis);
        } catch (Exception e) {
            this.redisFactory.returnBrokenResource(group, shardedJedis);
        }
        return result;
    }

    @Override
    public boolean put(String group, String key, Object value) {
        ShardedJedis shardedJedis = this.redisFactory.getJedisCache(group);
        if (shardedJedis == null) {
            return false;
        }

        boolean result = false;
        try {
            shardedJedis.set(SafeEncoder.encode(key), RedisUtil.seriallize(value));

            result = true;

            this.redisFactory.returnResource(group, shardedJedis);
        } catch (Exception e) {
            this.redisFactory.returnBrokenResource(group, shardedJedis);
        }
        return result;
    }

    @Override
    public boolean put(String group, String key, Object value, Date expiry) {
        ShardedJedis shardedJedis = this.redisFactory.getJedisCache(group);
        if (shardedJedis == null) {
            return false;
        }
        boolean result = false;
        try {
            int ttl = Integer.parseInt(String.valueOf((System.currentTimeMillis() - expiry.getTime()) / 1000L));

            if (ttl > 0) {
                shardedJedis.setex(SafeEncoder.encode(key), ttl, RedisUtil.seriallize(value));
            } else {
                shardedJedis.set(SafeEncoder.encode(key), RedisUtil.seriallize(value));
            }

            result = true;

            this.redisFactory.returnResource(group, shardedJedis);
        } catch (Exception e) {
            this.redisFactory.returnBrokenResource(group, shardedJedis);
        }
        return result;
    }

    @Override
    public boolean put(String group, String key, Object value, int ttl) {
        ShardedJedis shardedJedis = this.redisFactory.getJedisCache(group);
        if (shardedJedis == null) {
            return false;
        }
        boolean result = false;
        try {
            shardedJedis.setex(SafeEncoder.encode(key), ttl, RedisUtil.seriallize(value));
            result = true;
            this.redisFactory.returnResource(group, shardedJedis);
        } catch (Exception e) {
            this.redisFactory.returnBrokenResource(group, shardedJedis);
        }
        return result;
    }

    @Override
    public Object get(String group, String key) {
        ShardedJedis shardedJedis = this.redisFactory.getJedisCache(group);
        if (shardedJedis == null) {
            return null;
        }
        Object result = null;
        try {
            byte[] data = shardedJedis.get(SafeEncoder.encode(key));
            if (data != null) {
                result = RedisUtil.unseriallize(data);
            }
            this.redisFactory.returnResource(group, shardedJedis);
        } catch (Exception e) {
            this.redisFactory.returnBrokenResource(group, shardedJedis);
        }
        return result;
    }

    @Override
    public boolean remove(String group, String key) {
        ShardedJedis shardedJedis = this.redisFactory.getJedisCache(group);
        if (shardedJedis == null) {
            return false;
        }
        boolean result = false;
        try {
            shardedJedis.del(key);
            result = true;
            this.redisFactory.returnResource(group, shardedJedis);
        } catch (Exception e) {
            this.redisFactory.returnBrokenResource(group, shardedJedis);
        }
        return result;
    }

    @Override
    public int size(String group) {
        Set keySet = keySet(group);
        if (keySet == null) {
            return 0;
        }
        return keySet.size();
    }

    @Override
    public boolean isExist(String group, String key) {
        return checkObjectKeyExisted(group, key);
    }

    private boolean checkObjectKeyExisted(String group, String key) {
        ShardedJedis shardedJedis = this.redisFactory.getJedisCache(group);
        if (shardedJedis == null) {
            return false;
        }
        boolean result = false;
        try {
            result = shardedJedis.exists(key).booleanValue();
            this.redisFactory.returnResource(group, shardedJedis);
        } catch (Exception e) {
            this.redisFactory.returnBrokenResource(group, shardedJedis);
        }
        return result;
    }

    @Override
    public boolean add(String group, String key, Object value, int ttl) {
        if (isExist(group, key)) {
            return false;
        }
        return put(group, key, value, ttl);
    }
}
