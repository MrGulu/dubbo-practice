package cn.tang.base.dao;

import cn.tang.base.bean.APPL;
import java.math.BigDecimal;

public interface APPLMapper {
    int deleteByPrimaryKey(BigDecimal applSeq);

    int insert(APPL record);

    int insertSelective(APPL record);

    APPL selectByPrimaryKey(BigDecimal applSeq);

    int updateByPrimaryKeySelective(APPL record);

}