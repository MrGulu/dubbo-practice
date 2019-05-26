package cn.tang.base.service;

import cn.tang.base.bean.APPL;
import cn.tang.base.dao.APPLMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class ApplService implements IApplService {

    @Autowired
    private APPLMapper applMapper;

    @Override
    public APPL selectByApplSeq(BigDecimal applSeq) {
        return applMapper.selectByPrimaryKey(applSeq);
    }
}
