package cn.tang.base.activemq.sender;

import cn.tang.base.activemq.common.SchedulerMessagePostProcessor;
import org.apache.activemq.ScheduledMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jms.core.JmsMessagingTemplate;
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

    @Autowired
    @Qualifier("jmsMessagingTemplate")
    private JmsMessagingTemplate jmsMessagingTemplate;

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
     *
     * ----------该方法可以发送复杂类型参数，比如Map中value值为对象或者list！
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
     * @param queueName 队列名
     * @param message   发送队列参数
     * @param time      延迟发送，单位：ms
     * @description: 延迟发送Object类型参数
     */
    public void sendObjMessageWait(String queueName, final Object message, final long time) {
        jmsTemplate.send(queueName, new MessageCreator() {
            @Override
            public Message createMessage(Session session) throws JMSException {
                ObjectMessage mes = session.createObjectMessage((Serializable) message);
                mes.setLongProperty(ScheduledMessage.AMQ_SCHEDULED_DELAY, time);
                return mes;
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
     * @param queueName 队列名
     * @param message   发送队列参数
     * @param time      延迟发送，单位：ms
     * @description: 延迟发送MapMessage类型参数
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
     * @param time      延迟发送，单位：ms
     * @description: 延迟发送String类型参数
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

    /**
     * @description: 延迟发送obj类型参数，可以发送String，Bean及Map类型参数，消费者接受的时候原样类型
     * 接收即可！但是对于Map类型的数据，value值只能是java primitive类型！因为它会
     * 根据发送请求参数是Map而自动转成MapMessage，而MapMessage的value只能是java primitive类型！
     * <p>
     * 如果要想使用一个方法就发送所有数据，可以第一个方法sendObjMessage；
     * 下面两个方法是用于除复杂类型外的计划任务！可延迟发送或按照cron表达式。
     * 但是cron表达式
     * （注：此corn表达式并非Quartz框架中的corn表达式，而是linux中corntab中的表达 式，
     * 基本顺序是"分(0-59) 时(0-23) 日(1-31) 月（1-12） 星期几(1-7) （1就代表星期1）"）
     * 如果表达式为”12 12 12 12 *“ 其中星期字段是*，但是前面的分时日月字段已经明确指定了，
     * 那么在第一次执行完任务之后，下次执行的时候就是下一年的12月12日12时12分！注意理解。
     */
    public void sendObjectMessage(String queueName, final Object message) {
        jmsTemplate.convertAndSend(queueName, message);
    }

    public void sendObjectMessageSchedulerDelay(String queueName, final Object message, final long time) {
        jmsTemplate.convertAndSend(queueName, message, new SchedulerMessagePostProcessor(time));
    }

    public void sendObjectMessageSchedulerCron(String queueName, final Object message, final String cron) {
        jmsTemplate.convertAndSend(queueName, message, new SchedulerMessagePostProcessor(cron));
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
