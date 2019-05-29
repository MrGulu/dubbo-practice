package cn.tang.cacheframework.cache;

import cn.tang.cacheframework.rediscache.RedisManager;
import cn.tang.cacheframework.rediscache.ShardedJedislClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @description: cache工厂
 * @date 2018/7/10 9:36
 */
public class CacheAdminFactory {
    private static Logger logger = LoggerFactory.getLogger("CacheAdminFactory.class");

    private static CacheAdminFactory caf;
    private static ICacheClient cacheClient = null;
    private static ICacheAdmin cacheAdmin = null;
    private String clientType;
    private String configFile;

    private CacheAdminFactory(String clientType, String configFilePath) {
        logger.info("call CacheAdminFactory default constructor");
        this.clientType = clientType;
        this.configFile = configFilePath;
        init();
    }

    @SuppressWarnings("unused")
    public String getClientType() {
        return this.clientType;
    }

    private static synchronized void syncInit(String clientType, String configFilePath) {
        if (caf == null) {
            caf = new CacheAdminFactory(clientType, configFilePath);
        }
    }

    public static CacheAdminFactory getInstance(String clientType) {
        logger.info("call CacheAdminFactory getInstance  with " + clientType);
        if (caf == null) {
            syncInit(clientType, null);
        }
        return caf;
    }

    public static CacheAdminFactory getInstance(String clientType, String configFile) {
        logger.info("call CacheAdminFactory getInstance with " + clientType + " " + configFile);
        if (caf == null) {
            syncInit(clientType, configFile);
        }
        return caf;
    }

    private void init() {
        logger.debug("++++++++++cacheClient init ++++++++++");
        if (this.clientType.equalsIgnoreCase("redis")) {
            cacheAdmin = new RedisManager();
            if (this.configFile == null) {
                this.configFile = "redis.properties";
            }
        } else {
            logger.error(this.clientType + "not supported !!" + " change to default" + "memcached");
            this.clientType = "memcached";
            this.configFile = "memcached.xml";
        }
        logger.info("init " + this.clientType + " config file is " + this.configFile);
        cacheAdmin.init(this.configFile);
    }

    public ICacheClient getCacheClient() {
        logger.warn("++++++++++cacheClient init++++++++++");
        if (cacheClient == null) {
            if (this.clientType.equalsIgnoreCase("redis")) {
                cacheClient = new ShardedJedislClient();
            }
        }
        logger.info("get " + this.clientType + " CacheClient ");
        return cacheClient;
    }

}
