package cn.tang.base.service;

import cn.tang.base.bean.Appl;
import cn.tang.base.redis.bean.ApplAuthCache;
import org.springframework.web.bind.annotation.RequestParam;

import java.math.BigDecimal;
import java.util.Map;

public interface IApplService {

    Appl selectByApplSeq(BigDecimal applSeq);

    void removeRedisApplCache(@RequestParam("applSeq") String applSeq);

    int updateByPrimaryKeySelective(Appl record);

    int updateOutstsByPrimaryKey(Map<String, Object> params);

    /**
     * 根据业务流水号获取业务鉴权信息
     *
     * @param applSeq
     * @return
     */
    ApplAuthCache queryApplAuthByApplSeq(String applSeq);

}
