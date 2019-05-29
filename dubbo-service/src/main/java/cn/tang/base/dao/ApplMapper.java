package cn.tang.base.dao;

import cn.tang.base.bean.Appl;
import cn.tang.base.redis.bean.ApplAuthCache;

import java.math.BigDecimal;
import java.util.Map;

public interface ApplMapper {
    int deleteByPrimaryKey(BigDecimal applSeq);

    int insert(Appl record);

    int insertSelective(Appl record);

    Appl selectByPrimaryKey(BigDecimal applSeq);

    int updateByPrimaryKeySelective(Appl record);

    int updateOutstsByPrimaryKey(Map<String, Object> params);

    ApplAuthCache selectApplCacheByPrimaryKey(BigDecimal applSeq);

}
