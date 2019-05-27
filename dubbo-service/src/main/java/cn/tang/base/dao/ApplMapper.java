package cn.tang.base.dao;

import cn.tang.base.bean.Appl;

import java.math.BigDecimal;

/**
 * 使用mybatis插件生成
 */
public interface ApplMapper {
    int deleteByPrimaryKey(BigDecimal applSeq);

    int insert(Appl record);

    int insertSelective(Appl record);

    Appl selectByPrimaryKey(BigDecimal applSeq);

    int updateByPrimaryKeySelective(Appl record);

}