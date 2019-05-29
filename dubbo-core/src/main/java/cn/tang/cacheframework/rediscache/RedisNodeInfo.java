package cn.tang.cacheframework.rediscache;

import java.util.List;
import java.util.Map;

/**
 * @author tangwenlong
 * @description: redis节点信息类
 * @date 2018/7/10 9:28
 */
public class RedisNodeInfo {
    private List<ServerShardInfo> masters = null;
    private Map<String, List<ServerShardInfo>> slaves = null;
    private Map<String, List<ServerShardInfo>> invalidServer = null;

    public List<ServerShardInfo> getMasters() {
        return this.masters;
    }

    public void setMasters(List<ServerShardInfo> masters) {
        this.masters = masters;
    }

    public Map<String, List<ServerShardInfo>> getSlaves() {
        return this.slaves;
    }

    public void setSlaves(Map<String, List<ServerShardInfo>> slaves) {
        this.slaves = slaves;
    }

    public Map<String, List<ServerShardInfo>> getInvalidServer() {
        return this.invalidServer;
    }

    public void setInvalidServer(Map<String, List<ServerShardInfo>> invalidServer) {
        this.invalidServer = invalidServer;
    }
}
