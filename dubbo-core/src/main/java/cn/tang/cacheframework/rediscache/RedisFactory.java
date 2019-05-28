package cn.tang.cacheframework.rediscache;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.ShardedJedis;
import redis.clients.jedis.ShardedJedisPool;
import redis.clients.jedis.exceptions.JedisConnectionException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/**
 * @author yanglingxiao
 * @description: redis工厂类
 * @date 2018/7/10 14:07
 */
public class RedisFactory {
    private static Logger logger = LoggerFactory.getLogger(RedisFactory.class);

    private static Map<String, ShardedJedisPool> mapShardedJedisPool = null;
    private static Map<String, RedisNodeInfo> mapGroupNodeInfo = null;

    private final String DEFAULT_CONFIG_FILE = "redis.properties";
    private String configFile;
    private static RedisFactory redisFactory;
    private static JedisPoolConfig config = null;

    private RedisFactory(String configFilePath) {
        this.configFile = configFilePath;
        init();
    }

    private static synchronized void syncInit(String configFilePath) {
        if (redisFactory == null) {
            redisFactory = new RedisFactory(configFilePath);
        }
    }

    public static RedisFactory getInstance() {
        if (redisFactory == null) {
            syncInit(null);
        }
        return redisFactory;
    }

    public static RedisFactory getInstance(String configFilePath) {
        if (redisFactory == null) {
            syncInit(configFilePath);
        }
        return redisFactory;
    }

    public void reset(String configFilePath) {
        redisFactory.configFile = configFilePath;
        init();
    }

    public Map<String, ShardedJedisPool> getShardedJedisMap() {
        return mapShardedJedisPool;
    }

    @SuppressWarnings("unused")
    public Map<String, RedisNodeInfo> getMapGroupNodeInfo() {
        return mapGroupNodeInfo;
    }

    public Map<String, List<ServerShardInfo>> getJedisShardInfoMap() {
        Map groupMaster = new HashMap();
        for (Entry entry : mapGroupNodeInfo.entrySet()) {
            groupMaster.put(entry.getKey(), ((RedisNodeInfo) entry.getValue()).getMasters());
        }
        return groupMaster;
    }

    /**
     * 获取指定组Jedis访问对象
     */
    public ShardedJedis getJedisCache(String group) {
        ShardedJedis shardedJedis = null;
        ShardedJedisPool pool = getShardedJedisPool(group);
        if (pool != null) {
            try {
                shardedJedis = pool.getResource();
            } catch (JedisConnectionException e) {
                logger.error("can't get a shardedJedis from pool,will do try!");
                shardedJedis = doTry(group);
            }
        }
        return shardedJedis;
    }

    public ShardedJedisPool getShardedJedisPool(String group) {
        return mapShardedJedisPool.get(group);
    }

    private void init() {
        if (null == this.configFile) {
            this.configFile = DEFAULT_CONFIG_FILE;
        }
        InitConfig initConfig = new InitConfig(this.configFile);
        mapShardedJedisPool = initConfig.getMapShardedJedisPool();
        mapGroupNodeInfo = initConfig.getMapGroupNodeInfo();
        config = initConfig.getConfig();
    }

    public void returnResource(String group, ShardedJedis shardedJedis) {
        if (null != shardedJedis) {
            ShardedJedisPool shardedJedisPool = getShardedJedisPool(group);
            shardedJedisPool.returnResource(shardedJedis);
        }
    }

    public void returnBrokenResource(String group, ShardedJedis shardedJedis) {
        if (null != shardedJedis) {
            ShardedJedisPool shardedJedisPool = getShardedJedisPool(group);
            if (null != shardedJedisPool) {
                shardedJedisPool.returnBrokenResource(shardedJedis);
            }
        }
    }

    /**
     * 从ShardedJedisPool中获取shardedJedis，如果失败则进行切换主节点操作
     */
    private ShardedJedis doTry(String group) {
        ShardedJedis shardedJedis = null;
        ShardedJedisPool pool = getShardedJedisPool(group);
        try {
            shardedJedis = pool.getResource();
        } catch (JedisConnectionException e) {
            logger.error("fail to try get shardedJedis from pool,will try to do switch master!");
            //怎么会出这个异常？是一个master挂了还是全部挂了？
            shardedJedis = doChangeNode(group);
        }
        return shardedJedis;
    }

    private ShardedJedis doChangeNode(String group) {
        RedisNodeInfo nodeInfo = mapGroupNodeInfo.get(group);
        synchronized (nodeInfo) {
            SwitchMaster switchMaster = new SwitchMaster(group, nodeInfo, mapShardedJedisPool, config);
            switchMaster.doSwitch();
        }
        return getJedisCache(group);
    }

}

