package cn.tang.base.service;

import cn.tang.base.bean.Appl;
import cn.tang.base.dao.ApplMapper;
import cn.tang.base.redis.bean.ApplAuthCache;
import cn.tang.base.redis.service.IRedisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Map;

@Service
public class ApplService implements IApplService {

    @Autowired
    private ApplMapper applMapper;

    @Autowired
    private IRedisService redisService;

    @Override
    public Appl selectByApplSeq(BigDecimal applSeq) {
        return applMapper.selectByPrimaryKey(applSeq);
    }

    @Override
    public void removeRedisApplCache(String applSeq) {
        redisService.refreshApplCacheByApplSeq(applSeq);
    }

    @Override
    public ApplAuthCache queryApplAuthByApplSeq(String applSeq) {
        /**先从redis查询，若存在则取，否则查询本地入库到redis并返回*/
        ApplAuthCache applAuthCache = redisService.queryApplCacheByApplSeq(applSeq);
        if (null == applAuthCache) {
            applAuthCache = applMapper.selectApplCacheByPrimaryKey(new BigDecimal(applSeq));
            if (applAuthCache != null) {
                redisService.putApplCache(applSeq, applAuthCache);
            }
        }
        return applAuthCache;
    }

    @Override
    public int updateByPrimaryKeySelective(Appl record) {
        /**
         * 根据业务实体类刷新redis缓存
         */
        redisService.refreshApplCacheByAppl(record);
        return applMapper.updateByPrimaryKeySelective(record);
    }

    @Override
    public int updateOutstsByPrimaryKey(Map<String, Object> params) {
        /**
         * 根据业务流水号刷新redis缓存
         */
        redisService.refreshApplCacheByApplSeq(params.get("applSeq").toString());
        Appl appl = new Appl();
        String outSts = null;
        appl.setApplSeq(new BigDecimal(params.get("applSeq").toString()));
        if (params.get("outSts") != null) {
            outSts = params.get("outSts").toString();
        }
        appl.setOutSts(outSts);
        return applMapper.updateOutstsByPrimaryKey(params);
    }
}
