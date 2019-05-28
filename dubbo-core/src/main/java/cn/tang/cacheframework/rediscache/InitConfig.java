package cn.tang.cacheframework.rediscache;

import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.JedisShardInfo;
import redis.clients.jedis.ShardedJedisPool;

import java.util.*;
import java.util.Map.Entry;

/**
 * @author yanglingxiao
 * @description: 初始化配置
 * @date 2018/7/10 9:35
 */
public class InitConfig {

    private JedisPoolConfig config;
    private Map<String, ShardedJedisPool> mapShardedJedisPool;
    private Map<String, RedisNodeInfo> mapGroupNodeInfo;

    public InitConfig(String configFile) {
        init(configFile);
    }

    public JedisPoolConfig getConfig() {
        return this.config;
    }

    public Map<String, ShardedJedisPool> getMapShardedJedisPool() {
        return this.mapShardedJedisPool;
    }

    public Map<String, RedisNodeInfo> getMapGroupNodeInfo() {
        return this.mapGroupNodeInfo;
    }

    private void init(String configFile) {
        Properties properties = initProp(configFile);
        initPool(properties);
    }

    private void initPool(Properties properties) {
        this.mapShardedJedisPool = new HashMap();
        this.mapGroupNodeInfo = new HashMap();
        this.config = getJedisPoolConfig(properties);

        //生成主redis对照表(服务器名：地址)以及对应的从redis List对照表（服务器名;从redis服务器地址List）
        Map<String, List<String>> mapSlaveServers = new HashMap();
        Map<String, String> masterServers = getAllServer(properties, mapSlaveServers);

        //获取分组信息（组名：（节点名：节点权重） 列表）
        Map<String, List<String>> groups = getGroups(properties);

        List listGroupServer;
        String groupName;
        RedisNodeInfo redisNodeInfo;
        List<JedisShardInfo> shardsInfo;
        ShardedJedisPool shardedJedisPool;
        for (Entry entryGroup : groups.entrySet()) {
            groupName = (String) entryGroup.getKey();
            listGroupServer = (List) entryGroup.getValue();

            redisNodeInfo = getGroupRedisNodeInfo(listGroupServer, masterServers, mapSlaveServers);

            this.mapGroupNodeInfo.put(groupName, redisNodeInfo);
            shardsInfo = new ArrayList(redisNodeInfo.getMasters());

            shardedJedisPool = new ShardedJedisPool(this.config, shardsInfo);
            this.mapShardedJedisPool.put(groupName, shardedJedisPool);
        }
    }

    private Properties initProp(String configFile) {
        return null;
        // RedisFactory.class.getClassLoader().getResourceAsStream(configFile);

//        Properties properties = new Properties();
//        try {
//            InputStream fis = new FileInputStream( configFile );
//            properties.load(fis);
//        } catch (IOException e) {
//            throw new RuntimeException("redis config File is not exist", e);
//        }
//        return properties;
    }

    private JedisPoolConfig getJedisPoolConfig(Properties properties) {
        JedisPoolConfig config = new JedisPoolConfig();

        //最大连接数
        config.setMaxTotal(Integer.valueOf(properties.getProperty("jedis.pool.maxTotal", "1024")).intValue());
        //最大空闲数
        config.setMaxIdle(Integer.valueOf(properties.getProperty("jedis.pool.maxIdle", "200")).intValue());
        //无空闲连接时最大等待时间
        config.setMaxWaitMillis(Integer.valueOf(properties.getProperty("jedis.pool.maxWaitMills", "2000")).intValue());
        //指明是否在从池中取出连接前进行检验,如果检验失败,则从池中去除连接并尝试取出另一个
        config.setTestOnBorrow(Boolean.valueOf(properties.getProperty("jedis.pool.testOnBorrow", "true")).booleanValue());
        config.setTestOnReturn(Boolean.valueOf(properties.getProperty("jedis.pool.testOnReturn", "false")).booleanValue());

//        //最大连接数
//        config.setMaxTotal(Integer.valueOf(ApolloUtil.getConfig("jedis.pool.maxTotal")).intValue());
//        //最大空闲数
//        config.setMaxIdle(Integer.valueOf(ApolloUtil.getConfig("jedis.pool.maxIdle")).intValue());
//        //无空闲连接时最大等待时间
//        config.setMaxWaitMillis(Integer.valueOf(ApolloUtil.getConfig("jedis.pool.maxWaitMills")).intValue());
//        //指明是否在从池中取出连接前进行检验,如果检验失败,则从池中去除连接并尝试取出另一个
//        config.setTestOnBorrow(Boolean.valueOf(ApolloUtil.getConfig("jedis.pool.testOnBorrow")).booleanValue());
//        config.setTestOnReturn(Boolean.valueOf(ApolloUtil.getConfig("jedis.pool.testOnReturn")).booleanValue());

        return config;
    }

    /**
     * 从配置文件获取Server信息
     * redis.servers = servername1;servername2  #服务器名称
     * server.servername1.master = ip:port   主redis地址
     * server.servername1.slave  = ip:port;ip:port;ip:port   从redis地址，支持多个
     * <p>
     * 生成主redis对照表以及对应的从redis List对照表
     *
     * @param properties
     * @param mapSlaveServers <servername, ip:port >
     * @return
     */
    private Map<String, String> getAllServer(Properties properties, Map<String, List<String>> mapSlaveServers) {
        String serversProperty = "redis.servers";
        String serverMaster = "master";
        String serverSlave = "slave";
        String serverPrefix = "server";
        String serversStr = properties.getProperty(serversProperty);
//        String serversStr = ApolloUtil.getConfig(serversProperty);
        String[] serversName = serversStr.split(";");
        String proKey;
        String proValue;
        Map masterServers = new HashMap();
        for (String name : serversName) {
            proKey = serverPrefix + '.' + name + '.' + serverMaster;
            proValue = properties.getProperty(proKey);
//            proValue = ApolloUtil.getConfig(proKey);
            masterServers.put(name, proValue);

            proKey = serverPrefix + '.' + name + '.' + serverSlave;
            proValue = properties.getProperty(proKey);
//            proValue = ApolloUtil.getConfig(proKey);
            if (proValue != null) {
                List listSlave = mapSlaveServers.get(name);
                if (listSlave == null) {
                    listSlave = new ArrayList(2);
                    mapSlaveServers.put(name, listSlave);
                }
                for (String slave : proValue.split(";")) {
                    listSlave.add(slave);
                }
            }
        }
        return masterServers;
    }

    private Map<String, List<String>> getGroups(Properties properties) {
        String groupsProperty = "jedis.groups";
        String groupPrefix = "group";
        String groupsStr = properties.getProperty(groupsProperty);
//        String groupsStr = ApolloUtil.getConfig(groupsProperty);
        String[] groupsName = groupsStr.split(";");
        String proKey;
        String proValue;
        Map mapGroup = new HashMap();
        for (String name : groupsName) {
            proKey = groupPrefix + '.' + name;
            proValue = properties.getProperty(proKey);
//            proValue = ApolloUtil.getConfig(proKey);
            List list = (List) mapGroup.get(name);
            if (list == null) {
                list = new ArrayList(1);
                mapGroup.put(name, list);
            }
            for (String group : proValue.split(";")) {
                list.add(group);
            }
        }
        return mapGroup;
    }

    private RedisNodeInfo getGroupRedisNodeInfo(List<String> listGroupServer, Map<String, String> masterServers, Map<String, List<String>> mapSlaveServers) {
        List masters = new ArrayList();
        Map slaves = new HashMap();
        String[] serverInfo;
        String nodeName;
        int weight = 0;
        for (String groupServer : listGroupServer) {
            serverInfo = groupServer.split(":");
            nodeName = serverInfo[0];
            weight = Integer.parseInt(serverInfo[1]);
            ServerShardInfo masterShardInfo = getMasterShardInfo(nodeName, weight, masterServers);

            masters.add(masterShardInfo);

            List listSlaveInfo = getSlaveShardInfo(nodeName, weight, mapSlaveServers);

            if (listSlaveInfo != null) {
                slaves.put(nodeName, listSlaveInfo);
            }
        }
        RedisNodeInfo redisNodeInfo = new RedisNodeInfo();
        redisNodeInfo.setMasters(masters);
        redisNodeInfo.setSlaves(slaves);
        return redisNodeInfo;
    }

    private ServerShardInfo getMasterShardInfo(String nodeName, int weight, Map<String, String> masterServers) {
        String serverConfigStr = masterServers.get(nodeName);
        return newShardInfo(nodeName, serverConfigStr, weight);
    }

    private ServerShardInfo newShardInfo(String nodeName, String serverConfigStr, int weight) {
        String[] serverConfigs = serverConfigStr.split(":");
        ServerShardInfo serverShardInfo = new ServerShardInfo(serverConfigs[0], Integer.parseInt(serverConfigs[1]), 2000, weight);

        serverShardInfo.setNodeName(nodeName);
        return serverShardInfo;
    }

    private List<ServerShardInfo> getSlaveShardInfo(String nodeName, int weight, Map<String, List<String>> mapSlaveServers) {
        List<String> listSlave = mapSlaveServers.get(nodeName);
        if (listSlave != null) {
            List listResult = new ArrayList();
            for (String serverConfigStr : listSlave) {
                listResult.add(newShardInfo(nodeName, serverConfigStr, weight));
            }
            return listResult;
        }
        return null;
    }
}

