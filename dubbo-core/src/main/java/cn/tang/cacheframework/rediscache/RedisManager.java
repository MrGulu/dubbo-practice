package cn.tang.cacheframework.rediscache;


import cn.tang.cacheframework.cache.ICacheAdmin;
import cn.tang.cacheframework.model.CacheConnectionInfo;
import cn.tang.cacheframework.model.CacheServerInfo;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisShardInfo;
import redis.clients.jedis.ShardedJedisPool;

import java.util.*;

/**
 * @author tangwenlong
 * @description: redisManager
 * @date 2018/7/10 9:29
 */
public class RedisManager implements ICacheAdmin {
    private static RedisFactory redisFactory = null;

    @Override
    public void init() {
        redisFactory = RedisFactory.getInstance();
    }

    @Override
    public void init(String configFilePath) {
        redisFactory = RedisFactory.getInstance(configFilePath);
    }

    public boolean reload(String configFilePath) {
        if (redisFactory == null)
            init(configFilePath);
        else {
            redisFactory.reset(configFilePath);
        }
        return true;
    }

    @Override
    public void stop() {
        Map<String, ShardedJedisPool> shardedMap = redisFactory.getShardedJedisMap();

        if (shardedMap != null) {
            for (ShardedJedisPool shardedJedisPool : shardedMap.values()) {
                shardedJedisPool.destroy();
            }
        }
    }

    @SuppressWarnings("unused")
    private CacheConnectionInfo newConnectionInfo(String group, JedisShardInfo jedisShardInfo) {
        CacheConnectionInfo connectionInfo = new CacheConnectionInfo();
        connectionInfo.setGroup(group);
        connectionInfo.setHost(jedisShardInfo.getHost());
        connectionInfo.setPort(jedisShardInfo.getPort());
        connectionInfo.setWeight(jedisShardInfo.getWeight());
        connectionInfo.setTimeout(jedisShardInfo.getTimeout());
        return connectionInfo;
    }

    @SuppressWarnings("unused")
    private CacheServerInfo convert(Map<String, String> mapTemp) {
        CacheServerInfo cacheServerInfo = new CacheServerInfo();
        String userMemory = mapTemp.get("used_memory");
        if (userMemory != null) {
            cacheServerInfo.setUseMemory(Long.valueOf(userMemory).longValue());
        }
        String db = mapTemp.get("db0");
        if (db != null) {
            String[] dbKeys = db.split(",");
            String keys = dbKeys[0].split("=")[1];
            cacheServerInfo.setTotal(Long.valueOf(keys).longValue());
        }
        String hits = mapTemp.get("keyspace_hits");
        if (hits != null) {
            cacheServerInfo.setHits(Long.valueOf(hits).longValue());
        }
        String misses = mapTemp.get("keyspace_misses");
        if (misses != null) {
            cacheServerInfo.setMisses(Long.valueOf(misses).longValue());
        }
        String connectNo = mapTemp.get("connected_clients");
        if (connectNo != null) {
            cacheServerInfo.setConnectionNo(Integer.valueOf(connectNo).intValue());
        }
        cacheServerInfo.setAllInfo(mapTemp);
        return cacheServerInfo;
    }

    @Override
    public boolean clear(String group) {
        List<ServerShardInfo> listShardedInfo = (List) redisFactory.getJedisShardInfoMap().get(group);
        if (listShardedInfo == null) {
            return false;
        }
        Jedis jedis;
        boolean result;
        try {
            for (JedisShardInfo shardedInfo : listShardedInfo) {
                jedis = new Jedis(shardedInfo);
                jedis.flushAll();
                jedis.disconnect();
            }
            result = true;
        } catch (Exception e) {
            result = false;
        }
        return result;
    }

    @Override
    public Set<String> search(String group, String pattern) {
        List<ServerShardInfo> shardedJedis = redisFactory.getJedisShardInfoMap().get(group);

        Jedis jedis;
        Set setResult = new HashSet();
        Set setSearch;
        for (JedisShardInfo shardInfo : shardedJedis) {
            jedis = new Jedis(shardInfo);
            setSearch = jedis.keys(pattern);
            jedis.disconnect();
            if (setSearch != null) {
                setResult.addAll(setSearch);
            }
        }
        return setResult;
    }

    private ServerShardInfo getMaster(List<ServerShardInfo> masters, String nodeName) {
        for (ServerShardInfo serverShardInfo : masters) {
            if (serverShardInfo.getNodeName().equals(nodeName)) {
                return serverShardInfo;
            }
        }
        return null;
    }

    @SuppressWarnings("unused")
    private List<ServerShardInfo> validateAndAdd(RedisNodeInfo nodeInfo, String nodeName, List<ServerShardInfo> listInvalidServer) {
        List listResult = new ArrayList(1);
        ServerShardInfo master = getMaster(nodeInfo.getMasters(), nodeName);
        String host = master.getHost();
        int port = master.getPort();
        Iterator it = listInvalidServer.iterator();
        ServerShardInfo sever;
        boolean isValid;
        Jedis jedis;
        while (it.hasNext()) {
            sever = (ServerShardInfo) it.next();
            jedis = sever.createResource();
            isValid = RedisUtil.validateServer(jedis);
            if (isValid) {
                jedis.slaveof(host, port);
                jedis.disconnect();
                nodeInfo.getSlaves().get(nodeName).add(sever);
                it.remove();
                listResult.add(sever);
            }
        }
        return listResult;
    }

}
