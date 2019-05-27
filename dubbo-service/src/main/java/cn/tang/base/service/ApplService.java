package cn.tang.base.service;

import cn.tang.base.bean.Appl;
import cn.tang.base.dao.ApplMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class ApplService implements IApplService {

    @Autowired
    private ApplMapper applMapper;

    @Override
    public Appl selectByApplSeq(BigDecimal applSeq) {
        return applMapper.selectByPrimaryKey(applSeq);
    }
}
