package cn.tang.base.redis;

import cn.tang.cacheframework.model.RedisConstant;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class RedisHandle {

    private static final String DUBBO_WEB_PRE = "dubbo-web";

    @Autowired
    private RedisManager redisManager;

    public String redisTest(String key, String value) {
        log.info("Redis缓存key:{},value:{}", key, value);
        return (String) redisManager.getCache().put(DUBBO_WEB_PRE + RedisConstant.REDIS_SEPARATOR + key, value);
    }

}
