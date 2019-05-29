package cn.tang.base.redis.service;

import cn.tang.base.bean.Appl;
import cn.tang.base.redis.RedisManager;
import cn.tang.base.redis.bean.ApplAuthCache;
import cn.tang.utils.ApolloUtil;
import cn.tang.utils.StringUtils;
import com.alibaba.fastjson.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @description: redis服务
 **/
@Component
public class RedisService implements IRedisService {
    private static final Logger logger = LoggerFactory.getLogger(RedisService.class);
    private static final String APPL_PRE = "mdd-service:applCache:";

    @Autowired
    private RedisManager redisManager;

    /**
     * 将缓存的applCache取出，若缓存中不存在，返回null
     * 在ApplService中queryApplAuthByApplSeq（）方法使用
     *
     * @param applSeq
     * @return
     */
    @Override
    public ApplAuthCache queryApplCacheByApplSeq(String applSeq) {
        if (StringUtils.isEmpty(applSeq)) {
            return null;
        }
        String redisJson = (String) redisManager.getCache().get(APPL_PRE + applSeq);
        logger.info("{}获取Appl缓存值:[{}]", applSeq, redisJson);
        if (!StringUtils.isEmpty(redisJson)) {
            return JSONObject.parseObject(redisJson, ApplAuthCache.class);
        }
        return null;
    }

    /**
     * 将applCache放入缓存
     * 在ApplService中queryApplAuthByApplSeq()方法使用
     *
     * @param applSeq
     * @param applAuthCache
     * @return
     */
    @Override
    public String putApplCache(String applSeq, ApplAuthCache applAuthCache) {
        logger.info("{}存入Appl缓存值:[{}]", applSeq, applAuthCache);
        return redisManager.getCache().put(APPL_PRE + applSeq, JSONObject.toJSON(applAuthCache).toString(), Integer.parseInt(ApolloUtil.getConfig("param", "applCache.outTime"))).toString();
    }

    /**
     * 刷新redis缓存，最后其实是将其删除，最后需要用的时候会再从库中查出然后放入
     * 最后都是调用的下面refreshApplCacheByApplSeq()方法根据当时放入缓存的key值删除
     * <p>
     * 根据实体类更新的时候，如果实体类中的缓存字段的值跟缓存中相等，也就是没有变更，是不需要删除缓存的
     *
     * @param appl
     */
    @Override
    public void refreshApplCacheByAppl(Appl appl) {
        if (appl != null && appl.getApplSeq() != null) {
            String applSeq = appl.getApplSeq().toString();
            ApplAuthCache applAuthCache = queryApplCacheByApplSeq(applSeq);
            //注意equals的写法，不只是IDE生成，需要略作修改
            if (applAuthCache != null && !applAuthCache.equals(appl)) {
                refreshApplCacheByApplSeq(applSeq);
            } else {
                logger.info("{}存在的Appl缓存不存在或无更改，无需刷新，缓存值为：{}", applSeq, applAuthCache);
            }
        }
    }

    /**
     * 最终根据放入缓存的key值删除缓存的方法
     *
     * @param applSeq
     */
    @Override
    public void refreshApplCacheByApplSeq(String applSeq) {
        logger.info("{}存在的Appl缓存有变更，刷新结果:{}", applSeq, redisManager.getCache().remove(APPL_PRE + applSeq));
    }

}