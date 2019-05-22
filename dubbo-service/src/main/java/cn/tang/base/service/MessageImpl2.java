package cn.tang.base.service;

import org.springframework.stereotype.Service;

@Service
public class MessageImpl2 implements IMessage {
    @Override
    public String echo(String msg) {
        System.out.println("ECHO2222222:" + msg);
        return "ECHO2222222:" + msg;
    }
}
