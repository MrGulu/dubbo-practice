package cn.tang.base.activemq.consumer;

import cn.tang.base.bean.Appl;
import cn.tang.utils.JacksonUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * @author tangwenlong
 * @description: 此类中的实现mq消息监听的方式是在配置文件（spring-activeMq.xml）中指定处理具体队列的类ref 和方法 method。
 * 此种方式可以在一个类中实现监听多个队列！不会像实现MessageListener接口那样，一个类只能监听一个队列的消息！
 * 另外，使用此种方式，还会自动做参数的转换如下：
 * <p>
 * TextMessage转换为String对象；
 * <p>
 * BytesMessage转换为byte数组；
 * <p>
 * MapMessage转换为Map对象；
 * <p>
 * ObjectMessage转换为对应的Serializable对象。
 */
@Slf4j
@Component
public class TestMethodQueueConsumer {
    /**
     * TextMessage转换为String对象
     *
     * @param message
     */
    public void testMethodMqDefault(String message) {
        log.info("default++++++++++++++++++++++++++++++++");
        log.info("*************{}**************", message);
    }

    /**
     * MapMessage转换为Map对象
     *
     * @param map
     */
    public void testMethodMqMapMsg(Map<String, Object> map) {
        log.info("testMethodMqMapMsg接收map数据：" + map.toString());
        log.info("name:{}", map.get("name"));
    }

    /**
     * ObjectMessage转换为对应的Serializable对象
     *
     * @param appl
     */
    public void testMethodObjMsgMq(Appl appl) {
        log.info("testMethodObjMsgMq接收object数据：" + appl.toString());
    }

    /**
     * ObjectMessage转换为对应的Serializable对象
     *
     * @param map
     */
    public void testMethodObjMapMsgMq(Map<String, Object> map) {
        log.info("testMethodObjMapMsgMq接收复杂obj数据：" + map.toString());
        log.info("复杂obj类型转json格式输出:{}", JacksonUtil.mapToJson(map));
    }

    /**
     * ObjectMessage转换为对应的Serializable对象
     *
     * @param map
     */
    public void testMethodObjMapListMsgMq(Map<String, Object> map) {
        log.info("testMethodObjMapMsgMq接收复杂obj数据：" + map.toString());
        log.info("复杂obj类型转json格式输出:{}", JacksonUtil.mapToJson(map));
    }


    /**
     * ObjectMessage转换为对应的Serializable对象
     *
     * @param map
     */
    @JmsListener(destination = "annotation.queue")
    public void testMethodMqAnno(Map<String, Object> map) {
        log.info("@JmsListener注解接收消息~~~~~~~~~~");
        log.info("@JmsListener注解接收复杂obj数据：" + map.toString());
        log.info("@JmsListener注解接收数据转json格式输出:{}", JacksonUtil.mapToJson(map));
        log.info("@JmsListener注解接收消息~~~~~~~~~~**********************************");
    }
}
