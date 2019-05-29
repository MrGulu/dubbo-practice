package cn.tang.base.redis;

import cn.tang.cacheframework.cache.ICacheClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.Collections;
import java.util.Set;

/**
 * @author tangwenlong
 * redis缓存客户端
 */
public class RedisClient {
    private static Logger logger = LoggerFactory.getLogger("RedisClient.class");

    private ICacheClient cacheClient;
    private String group;

    public RedisClient(String group, ICacheClient cacheClient) {
        this.group = group;
        this.cacheClient = cacheClient;
    }

    public void setCacheClient(ICacheClient cacheClient) {
        this.cacheClient = cacheClient;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public Object get(String key) {
        return cacheClient.get(group, key);
    }

    public Object put(String key, Object value) {
        return cacheClient.put(group, key, value) ? value : null;
    }

    public Object put(String key, Object value, Integer ttl) {
        return cacheClient.put(group, key, value, ttl) ? value : null;
    }

    public Object setSign(String key, Object value, Integer ttl) {
        if (cacheClient.put(group, key, value, ttl)) {
            return value;
        } else {
            return null;
        }
    }

    public Boolean isExits(String key) {
        return cacheClient.isExist(group, key);
    }

    public Boolean remove(String key) {
        return cacheClient.remove(group, key);
    }

    public void clear() {
        cacheClient.clear(group);
    }

    public int size() {
        return cacheClient.size(group);
    }

    public Set<String> keys() {
        try {
            return Collections.emptySet();
        } catch (Exception e) {
            logger.error("get values error" + e.toString());
            return null;
        }
    }

    public Collection<Object> values() {
        try {
            return Collections.emptyList();
        } catch (Exception e) {
            logger.error("get values error" + e.toString());
            return null;
        }
    }

}
