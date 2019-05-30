package cn.tang.base.service;

import org.springframework.stereotype.Service;

@Service
public class MessageServiceImpl2 implements IMessageService {
    @Override
    public String echo(String msg) {
        System.out.println("ECHO2222222:" + msg);
        return "ECHO2222222:" + msg;
    }
}
