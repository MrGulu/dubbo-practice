package cn.tang.base.service;

import cn.tang.base.bean.APPL;

import java.math.BigDecimal;

public interface IApplService {

    APPL selectByApplSeq(BigDecimal applSeq);

}
