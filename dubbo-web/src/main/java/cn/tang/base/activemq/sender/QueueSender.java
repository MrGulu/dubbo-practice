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

    /**
     * @param queueName 队列名
     * @param message   发送队列参数
     * @description: 这里使用        ObjectMessage mes = session.createObjectMessage((Serializable)message);
     * 所以在调用这个方法的时候，只需要传入Object类型对象就可以，但是如果在调用方法的时候使用了
     * ObjectMessage objectMessage = new ActiveMQObjectMessage();
     * objectMessage.setObject(appl);
     * 那么在方法中就直接return message就可以了！！！切记！！！！！！！！！！！！！！！！！！！！！！！！
     * <p>
     * 注意：最需要注意的地方就是在session.createObjectMessage((Serializable)message)这个操作中，
     * 参数必须实现Serializable接口！！！否则就汇报类型转换异常！！！
     * Map<String,Object> map = new HashMap<>();这种形式组织的参数也是没问题的，因为它实际创建
     * 的还是一个HashMap对象，而HashMap类已经实现了Serializable接口！
     */
    public void sendObjMessage(String queueName, final Object message) {
        jmsTemplate.send(queueName, new MessageCreator() {
            @Override
            public Message createMessage(Session session) throws JMSException {
                return session.createObjectMessage((Serializable) message);
            }
        });
    }

    /**
     * @description: 延迟发送Object类型参数
     * @param queueName 队列名
     * @param message 发送队列参数
     * @param time 延迟发送，单位：ms
     */
    public void sendObjMessageWait(String queueName, final Object message, final long time) {
        jmsTemplate.send(queueName, new MessageCreator() {
            @Override
            public Message createMessage(Session session) throws JMSException {
                ObjectMessage mes = session.createObjectMessage((Serializable) message);
                mes.setLongProperty(ScheduledMessage.AMQ_SCHEDULED_DELAY, time);
                return session.createObjectMessage((Serializable) message);
            }
        });
    }

    /**
     * @param queueName 队列名
     * @param message   发送队列参数
     * @description: 发送MapMessage类型参数，放入的value值必须是java primitive类型！
     */
    public void sendMapMessage(String queueName, final MapMessage message) {
        jmsTemplate.send(queueName, new MessageCreator() {
            @Override
            public Message createMessage(Session session) throws JMSException {
                return message;
            }
        });
    }

    /**
     * @description: 延迟发送MapMessage类型参数
     * @param queueName 队列名
     * @param message 发送队列参数
     * @param time 延迟发送，单位：ms
     */
    public void sendMapMessageWait(String queueName, final MapMessage message, final long time) {
        jmsTemplate.send(queueName, new MessageCreator() {
            @Override
            public Message createMessage(Session session) throws JMSException {
                message.setLongProperty(ScheduledMessage.AMQ_SCHEDULED_DELAY, time);
                return message;
            }
        });
    }

    /**
     * @description: 发送String类型参数
     * @param queueName 队列名
     * @param message 发送队列参数
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
     * @description: 延迟发送String类型参数
     * @param queueName 队列名
     * @param message 发送队列参数
     * @param time 延迟发送，单位：ms
     */
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
