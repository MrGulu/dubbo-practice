package cn.tang.base.activemq.consumer;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.jms.MapMessage;
import javax.jms.Message;
import javax.jms.MessageListener;

/**
 * @author tangwenlong
 * @description: 队列测试消费者
 */

/**
 * 在使用实现MessageListener接口，重写onMessage(Message message)方法的形式监听队列消息时，
 * 在此类中就已经不能再写其他的普通方法，然后在配置文件中指定ref 和 method，不会起作用，不会进入队列！
 */
@Slf4j
@Component("testQueueConsumer")
public class TestQueueConsumer implements MessageListener {

    @Override
    public void onMessage(Message message) {
        if (message instanceof MapMessage) {
            MapMessage mapMessage = (MapMessage) message;
            log.info("MQ-取得队列消息：{}", message);
            log.info("MQ-取得队列消息：{}", mapMessage);
            try {
                String name = mapMessage.getString("name");
                String age = mapMessage.getString("age");
                log.info("name:{},age:{}", name, age);
            } catch (Exception e) {
                log.error("MQ-异常！", e);
            }
        }
    }
}
