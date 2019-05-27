package cn.tang.base.dao;

import cn.tang.base.bean.Appl;

import java.math.BigDecimal;

public interface ApplMapper {
    int deleteByPrimaryKey(BigDecimal applSeq);

    int insert(Appl record);

    int insertSelective(Appl record);

    Appl selectByPrimaryKey(BigDecimal applSeq);

    int updateByPrimaryKeySelective(Appl record);

}
