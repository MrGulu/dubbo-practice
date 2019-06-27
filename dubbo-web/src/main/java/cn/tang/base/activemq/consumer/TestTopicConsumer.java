package cn.tang.base.activemq.consumer;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.jms.*;

/**
 * @author tangwenlong
 */
@Slf4j
@Component("testTopicConsumer")
public class TestTopicConsumer implements MessageListener {

    @Override
    public void onMessage(Message message) {
        if (message instanceof TextMessage) {
            try {
                log.info("***********testTopicConsumer*message:{}***********",((TextMessage) message).getText());
            } catch (JMSException e) {
                log.error("catch~~~~~~~~~~~~~~~~");
            }
        }
    }
}
