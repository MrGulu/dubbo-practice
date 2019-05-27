package cn.tang.base.service;

import cn.tang.base.bean.Appl;

import java.math.BigDecimal;

public interface IApplService {

    Appl selectByApplSeq(BigDecimal applSeq);

}
