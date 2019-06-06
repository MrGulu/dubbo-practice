package cn.tang.base.activemq.common;

import cn.tang.utils.StringUtils;
import org.apache.activemq.ScheduledMessage;
import org.springframework.jms.core.MessagePostProcessor;

import javax.jms.JMSException;
import javax.jms.Message;

/**
 * @author tangwenlong
 * @description: 自定义计划任务消息后处理器
 */
public class SchedulerMessagePostProcessor implements MessagePostProcessor {

    private long delay = 0L;

    private String cronDelay = null;

    public SchedulerMessagePostProcessor(long delay) {
        this.delay = delay;
    }

    public SchedulerMessagePostProcessor(String cron) {
        this.cronDelay = cron;
    }

    @Override
    public Message postProcessMessage(Message message) throws JMSException {
        if (delay > 0) {
            message.setLongProperty(ScheduledMessage.AMQ_SCHEDULED_DELAY, delay);
        } else if (!StringUtils.isEmpty(cronDelay)) {
            message.setStringProperty(ScheduledMessage.AMQ_SCHEDULED_CRON, cronDelay);
        }
        return message;
    }
}
