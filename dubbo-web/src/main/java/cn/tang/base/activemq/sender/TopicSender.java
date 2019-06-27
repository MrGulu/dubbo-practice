package cn.tang.base.activemq.sender;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jms.core.JmsMessagingTemplate;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.springframework.stereotype.Component;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;

/**
 * @author : tangwenlong
 * @description: 队列发送类
 */
@Component("topicSender")
public class TopicSender {

    @Autowired
    @Qualifier("jmsTopicTemplate")
    private JmsTemplate jmsTemplate;

    @Autowired
    @Qualifier("jmsMessagingTemplate")
    private JmsMessagingTemplate jmsMessagingTemplate;

    /**
     * @param queueName 队列名
     * @param message   发送队列参数
     * @description: 发送String类型参数
     */
    public void sendString(String queueName, final String message) {
        jmsTemplate.send(queueName, new MessageCreator() {
            @Override
            public Message createMessage(Session session) throws JMSException {
                return session.createTextMessage(message);
            }
        });
    }

    /**
     * @param queueName 队列名
     * @param message   发送队列参数
     * @description: 使用jmsMessagingTemplate发送String类型参数
     * 注：用上面jmsTemplate就行了。
     */
    public void sendStringJms(String queueName, final String message) {
        jmsMessagingTemplate.convertAndSend(queueName, message);
    }


}
