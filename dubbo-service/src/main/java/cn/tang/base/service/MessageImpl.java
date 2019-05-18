package cn.tang.base.service;

import org.springframework.stereotype.Service;
//定义的bean的名称为messageImpl（首字母小写）
@Service
public class MessageImpl implements IMessage{
    @Override
    public String echo(String msg) {
        return "ECHO:" + msg;
    }
}
