package cn.tang.base.service;

public interface IMessageService {
    /**
     * 实现消息的回应处理
     * @param msg
     * @return 处理后的echo数据
     */
    String echo(String msg);
}
