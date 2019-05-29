package cn.tang.base.activemq.sender;

import org.apache.activemq.ScheduledMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.springframework.stereotype.Component;

import javax.jms.*;
import java.io.Serializable;

/**
 * @author : tangwenlong
 * @description: 队列发送类
 */
@Component("queueSender")
public class QueueSender {

    @Autowired
    @Qualifier("jmsQueueTemplate")
    private JmsTemplate jmsTemplate;

    public void send(String queueName, final Object message) {
        jmsTemplate.send(queueName, new MessageCreator() {
            @Override
            public Message createMessage(Session session) throws JMSException {
                ObjectMessage mes = session.createObjectMessage((Serializable) message);
                return mes;
            }
        });
    }

    public void sendMapMessage(String queueName, final MapMessage message) {
        jmsTemplate.send(queueName, new MessageCreator() {
            @Override
            public Message createMessage(Session session) throws JMSException {
                return message;
            }
        });
    }

    public void sendMapMessageWait(String queueName, final MapMessage message, final long time) {
        jmsTemplate.send(queueName, new MessageCreator() {
            @Override
            public Message createMessage(Session session) throws JMSException {
                message.setLongProperty(ScheduledMessage.AMQ_SCHEDULED_DELAY, time);
                return message;
            }
        });
    }

    public void sendString(String queueName, final String message) {
        jmsTemplate.send(queueName, new MessageCreator() {
            @Override
            public Message createMessage(Session session) throws JMSException {
                return session.createTextMessage(message);
            }
        });
    }

    public void sendStringWait(String queueName, final String message, final long time) {
        jmsTemplate.send(queueName, new MessageCreator() {
            @Override
            public Message createMessage(Session session) throws JMSException {
                TextMessage mes = session.createTextMessage(message);
                mes.setLongProperty(ScheduledMessage.AMQ_SCHEDULED_DELAY, time);
                return mes;
            }
        });
    }
}
