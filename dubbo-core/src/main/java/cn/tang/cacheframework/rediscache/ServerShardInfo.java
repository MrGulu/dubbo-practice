package cn.tang.cacheframework.rediscache;

import redis.clients.jedis.JedisShardInfo;

/**
 * @author tangwenlong
 * @description: 节点信息
 * @date 2018/7/10 14:25
 */
public class ServerShardInfo extends JedisShardInfo {

    /**
     * @description: 节点名称
     * @author tangwenlong
     * @date 2018/7/10 14:24
     */
    private String nodeName;

    /**
     * @description: 分片节点信息
     * @author tangwenlong
     * @date 2018/7/10 14:24
     */
    public ServerShardInfo(String host, int port, int timeout, int weight) {
        super(host, port, timeout, weight);
    }

    public String getNodeName() {
        return this.nodeName;
    }

    public void setNodeName(String nodeName) {
        this.nodeName = nodeName;
    }

}
