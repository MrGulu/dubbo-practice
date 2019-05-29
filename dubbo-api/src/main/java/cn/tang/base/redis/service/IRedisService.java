package cn.tang.base.redis.service;


import cn.tang.base.bean.Appl;
import cn.tang.base.redis.bean.ApplAuthCache;

public interface IRedisService {

    /**
     * 根据业务流水号获取业务鉴权信息
     *
     * @param applSeq
     * @return
     */
    ApplAuthCache queryApplCacheByApplSeq(String applSeq);

    /**
     * 根据业务流水号及业务鉴权实体类存入redis
     *
     * @param applSeq
     * @param applAuthCache
     * @return
     */
    String putApplCache(String applSeq, ApplAuthCache applAuthCache);

    /**
     * 根据业务实体类刷新redis缓存
     *
     * @param appl
     * @return
     */
    void refreshApplCacheByAppl(Appl appl);

    /**
     * 根据业务流水号刷新redis缓存
     *
     * @param applSeq
     */
    void refreshApplCacheByApplSeq(String applSeq);

}
