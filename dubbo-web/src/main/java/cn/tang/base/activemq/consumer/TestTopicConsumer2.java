package cn.tang.base.activemq.consumer;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;

/**
 * @author tangwenlong
 */
@Slf4j
@Component("testTopicConsumer2")
public class TestTopicConsumer2 implements MessageListener {

    @Override
    public void onMessage(Message message) {
        if (message instanceof TextMessage) {
            try {
                log.info("***********testTopicConsumer2*message:{}***********",((TextMessage) message).getText());
            } catch (JMSException e) {
                log.error("catch~~~~~~~~~~~~~~~~");
            }
        }
    }
}
