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
