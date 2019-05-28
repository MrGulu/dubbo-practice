package cn.tang.cacheframework.rediscache;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.JedisShardInfo;
import redis.clients.jedis.ShardedJedisPool;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 切换主节点
 */

/**
 * @author yanglingxiao
 * @description: 切换主节点
 * @date 2018/7/10 13:37
 */
public class SwitchMaster {
    private static Logger logger = LoggerFactory.getLogger(SwitchMaster.class);
    private String group;
    private RedisNodeInfo nodeInfo;
    private Map<String, ShardedJedisPool> mapShardedJedisPool;
    private JedisPoolConfig config;

    public SwitchMaster(String group, RedisNodeInfo nodeInfo, Map<String, ShardedJedisPool> mapShardedJedisPool, JedisPoolConfig config) {
        this.group = group;
        this.nodeInfo = nodeInfo;
        this.mapShardedJedisPool = mapShardedJedisPool;
        this.config = config;
    }


    /**
     * @description: 执行节点切换, 找到第一个无效master的index, 将无效master加入无效服务器列表, 并进行？？？？
     * @author yanglingxiao
     * @date 2018/7/10 15:35
     */
    public void doSwitch() {
        List masters = this.nodeInfo.getMasters();
        int index = findInvalidServer(masters);
        if (index > -1) {
            ServerShardInfo masterServer = (ServerShardInfo) masters.remove(index);
            doSwitchMaster(this.group, this.nodeInfo, index, masterServer);
        }
    }

    private int findInvalidServer(List<ServerShardInfo> masters) {
        Jedis jedis;
        boolean isValid;
        int index = -1;
        for (int i = 0; i < masters.size(); i++) {
            //测试redis实例是否有效，直接
            jedis = (masters.get(i)).createResource();
            isValid = validateServer(jedis);
            if (!isValid) {
                index = i;
                break;
            }
        }
        return index;
    }

    private boolean validateServer(Jedis jedis) {
        boolean result = false;
        try {
            if (jedis.ping().equals("PONG")) {
                result = true;
            }
        } catch (Exception ex) {
            logger.error("Timeout connecting to the redis server", ex);
        }
        return result;
    }


    /**
     * 将无效masterServer加入invalidList
     * 遍历无效节点的slaveList，从中找到可用slave
     * 使用slaveofNoOne命令使之成为新master
     * 将salveList的其他slave调整新master
     * 重新生成连接池
     */
    private void doSwitchMaster(String group, RedisNodeInfo nodeInfo, int index, ServerShardInfo masterServer) {
        String nodeName = masterServer.getNodeName();

        List listInvalid = addInvalidServer(nodeInfo, masterServer);

        List listSlave = nodeInfo.getSlaves().get(nodeName);
        ServerShardInfo slave = null;
        Jedis slaveJedis = null;

        if (listSlave != null) {
            for (int i = listSlave.size() - 1; i >= 0; i--) {
                slave = (ServerShardInfo) listSlave.remove(i);
                slaveJedis = slave.createResource();
                if (validateServer(slaveJedis)) {
                    break;
                }
                listInvalid.add(slave);
                slave = null;
            }
        }

        if (slave == null) {
            String msg = "Redis server is died,And no slave can use.";
            logger.error(msg);
            throw new RuntimeException(msg);
        }
        slaveJedis.slaveofNoOne();
        changeSlave(slave, listSlave);
        List masters = nodeInfo.getMasters();
        //必须将新master放到list原来的位置，否则影响分片结果！！！
        masters.add(index, slave);
        ShardedJedisPool pool = this.mapShardedJedisPool.get(group);
        pool.destroy();
        ArrayList lsitInfo = new ArrayList(masters);

        pool = new ShardedJedisPool(this.config, lsitInfo);
        this.mapShardedJedisPool.put(group, pool);
    }

    private List<ServerShardInfo> addInvalidServer(RedisNodeInfo nodeInfo, ServerShardInfo masterServer) {
        Map invalidMap = nodeInfo.getInvalidServer();

        if (invalidMap == null) {
            invalidMap = new HashMap();
            nodeInfo.setInvalidServer(invalidMap);
        }
        String nodeName = masterServer.getNodeName();
        List listInvalid = (List) invalidMap.get(nodeName);
        if (listInvalid == null) {
            listInvalid = new ArrayList(1);
            invalidMap.put(nodeName, listInvalid);
        }

        listInvalid.add(masterServer);
        return listInvalid;
    }

    private void changeSlave(ServerShardInfo slave, List<ServerShardInfo> listSlave) {
        String masterHost = slave.getHost();
        int masterPort = slave.getPort();
        JedisShardInfo otherSlave;
        Jedis otherSlaveJedis;
        for (int i = listSlave.size() - 1; i >= 0; i--) {
            otherSlave = listSlave.get(i);
            otherSlaveJedis = otherSlave.createResource();
            otherSlaveJedis.slaveof(masterHost, masterPort);
            otherSlaveJedis.disconnect();
        }
    }

}
