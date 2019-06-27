package cn.tang.base.activemq.consumer;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class TestMethodTopicConsumer {
    /**
     * TextMessage转换为String对象
     *
     * @param message
     */
    @JmsListener(destination = "annotation.topic")
    public void testMethodMqDefault(String message) {
        log.info("default++++++++++++++++++++++++++++++++");
        log.info("*************{}**************", message);
    }

//    @JmsListener(destination = "annotation.topic")
//    public void testMethodMqDefault2(String message) {
//        log.info("default2++++++++++++++++++++++++++++++++");
//        log.info("*************{}**************", message);
//    }
//
//    @JmsListener(destination = "annotation.topic")
//    public void testMethodMqDefault3(String message) {
//        log.info("default3++++++++++++++++++++++++++++++++");
//        log.info("*************{}**************", message);
//    }

}
