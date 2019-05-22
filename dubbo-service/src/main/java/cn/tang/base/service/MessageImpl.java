package cn.tang.base.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
//定义的bean的名称为messageImpl（首字母小写）
@Service
public class MessageImpl implements IMessage{

    public static final Logger logger = LoggerFactory.getLogger(MessageImpl.class);

    @Override
    public String echo(String msg) {
        System.out.println("ECHO:" + msg);
        logger.info("logger->ECHO:" + msg);
        return "ECHO:" + msg;
    }
}
