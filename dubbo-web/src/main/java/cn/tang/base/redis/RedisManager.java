package cn.tang.base.redis;

import cn.tang.cacheframework.cache.ICacheClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author tangwenlong
 * @description: redis缓存管理器
 * @date 2019/5/28
 */
public class RedisManager {
    @SuppressWarnings("unused")
    private static Logger logger = LoggerFactory.getLogger("RedisManager.class");

    private RedisClient redisClient;
    private String group;
    private ICacheClient cacheClient;

    public RedisManager() {
    }

    public void setCacheClient(ICacheClient cacheClient) {
        this.cacheClient = cacheClient;
    }

    @SuppressWarnings("unused")
    public void setRedisClient(RedisClient redisClient) {
        this.redisClient = redisClient;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public void destroy() {
        redisClient = null;
        group = null;
        cacheClient = null;
    }

    public RedisClient getCache() {
        if (redisClient == null) {
            this.redisClient = new RedisClient(group, cacheClient);
        }
        return this.redisClient;
    }

}
