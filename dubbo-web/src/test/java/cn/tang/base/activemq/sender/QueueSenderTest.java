package cn.tang.base.activemq.sender;

import cn.tang.base.activemq.common.MqConstant;
import cn.tang.base.bean.Appl;
import lombok.extern.slf4j.Slf4j;
import org.apache.activemq.command.ActiveMQObjectMessage;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.jms.ObjectMessage;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 书写测试类遇到的几点错误：
 * 1.一开始未加@RunWith(SpringJUnit4ClassRunner.class)，导致错误：
 * java.lang.NullPointerException
 * at cn.tang.base.activemq.sender.QueueSenderTest.test(QueueSenderTest.java:30)
 * 如果不加@RunWith注解，可以让当前类继承AbstractJUnit4SpringContextTests类
 * 2.一开始在spring-test.xml配置如下，没问题
 * <context:component-scan base-package="cn.tang">
 * <context:exclude-filter type="annotation"
 * expression="org.springframework.stereotype.Controller"/>
 * <context:exclude-filter type="annotation"
 * expression="org.springframework.web.bind.annotation.ControllerAdvice"/>
 * </context:component-scan>
 * 但是如果改为下面：
 * <context:component-scan base-package="cn.tang"/>
 * 则会导致错误：
 * java.lang.IllegalStateException: Failed to load ApplicationContext
 * Caused by: org.springframework.beans.factory.UnsatisfiedDependencyException:
 * Error creating bean with name 'redisController': Unsatisfied dependency expressed
 * through field 'applService'; nested exception is org.springframework.beans.factory.NoSuchBeanDefinitionException:
 * No qualifying bean of type 'cn.tang.base.service.IApplService' available:
 * expected at least 1 bean which qualifies as autowire candidate.
 * Dependency annotations: {@org.springframework.beans.factory.annotation.Autowired(required=true)}
 * 3.在spring-test.xml中配置了<context:component-scan base-package="cn.tang"/>，但是又使用
 * <bean id="queueSender" class="cn.tang.base.activemq.sender.QueueSender"/>重新将QueueSender
 * 注入spring容器（ApplicationContext），导致错误：
 * java.lang.IllegalStateException: Failed to load ApplicationContext
 * 但是后来发现，即使重复注册bean，也不会出问题，是上面2的原因导致！
 * 4.@ContextConfiguration注解未引入spring-redis.xml配置文件，导致错误：
 * java.lang.IllegalStateException: Failed to load ApplicationContext
 * Caused by: org.springframework.beans.factory.UnsatisfiedDependencyException:
 * Error creating bean with name 'redisHandle': Unsatisfied dependency expressed
 * through field 'redisManager'; nested exception is org.springframework.beans.factory.NoSuchBeanDefinitionException:
 * No qualifying bean of type 'cn.tang.base.redis.RedisManager' available:
 * expected at least 1 bean which qualifies as autowire candidate. Dependency annotations: {@org.springframework.beans.factory.annotation.Autowired(required=true)}
 * 5.在加载了spring-redis.xml之后，未在VM options加入 -DCONF_HOME=E:\workspace\conf_home\dubbo-conf,导致错误：
 * java.lang.IllegalStateException: Failed to load ApplicationContext
 * Caused by: java.lang.IllegalArgumentException: Could not resolve placeholder 'CONF_HOME' in string value "${CONF_HOME}/redis.properties"
 * 总结：
 * 2和4，本质上是一个问题导致，都是因为在配置了扫描注解之后，容器会到指定的package下寻找注解，在发现一个类上
 * 有注解时，就会将这个类注册到容器当中，在注册的过程中，如果该类中存在类成员使用@AutoWired注解注入到本类中，
 * 那么spring还会在容器中寻找这个bean，但是都无法找到，才导致错误。
 * 对于2，是因为如果不剔除@Controller注解的话，所有加了这个注解的类，肯定是要使用service作为类成员的，但是
 * 在项目启动过程中是通过dubbo框架来注册service中相关bean的，在这个测试类中只是测试mq，并不需要service，所以
 * 并没有加载dubbo的相关配置文件，况且如果需要测试，肯定是要启动service项目的！综上导致错误！
 * 对于4，是因为在扫描到RedisHandle类上有@Component注解，加载类成员时，在spring容器中也是无法找到，而redisManager
 * 这个成员所属类RedisManager是通过spring-redis.xml配置文件<bean></bean>标签注入的，在未加载spring-redis.xml
 * 时，肯定RedisManager类是没有注册到spring容器的！综上导致错误！
 */
@Slf4j
@RunWith(SpringJUnit4ClassRunner.class)
//或者继承AbstractJUnit4SpringContextTests类
@ContextConfiguration(locations = {"classpath:spring-test.xml",
        "classpath:spring-activeMq.xml",
        "classpath:spring-redis.xml"})
@SuppressWarnings("all")
public class QueueSenderTest {

    @Autowired
    private QueueSender queueSender;

    @Autowired
    private TopicSender topicSender;

    /**
     * 测试原来方式发送各种类型参数
     * jmsTemplate.send（……）
     */
    @Test
    public void test() {
        try {
            Appl appl = new Appl();
            appl.setApplSeq(new BigDecimal("6733456"));
            appl.setOutSts("14");
            List<Appl> list = new ArrayList<>();
            list.add(appl);
            Map<String, Object> map = new HashMap<>();
            Map<String, Object> map2 = new HashMap<>();
            map.put("name", "aaa");
            map.put("age", 88);
            map2.put("name", "bbb");
            map2.put("age", 88);
            map2.put("list", list);
            //发送string数据
            queueSender.sendObjMessage(MqConstant.TEST_METHOD_DEFAULT_QUEUE, "hahaha");
            //发送bean数据
            queueSender.sendObjMessage(MqConstant.TEST_METHOD_OBJMSG_QUEUE, appl);
            //发送简单Map数据（value值只包含java primitive类型）
            queueSender.sendObjMessage(MqConstant.TEST_METHOD_MAPMSG_COMPLEX_QUEUE, map);
            //发送复杂Map数据（value值可以包含对象，List等。）
            queueSender.sendObjMessage(MqConstant.TEST_METHOD_MAPMSG_COMPLEX_QUEUE, map2);
//            queueSender.sendObjMessageWait(MqConstant.TEST_METHOD_MAPMSG_COMPLEX_QUEUE, map,10000L);
        } catch (Exception e) {
            log.error("队列发送时异常！", e);
        }
    }

    /**
     * 测试新方法发送各种类型参数
     * jmsTemplate.convertAndSend（……）
     */
    @Test
    public void test1() {
        try {
            Appl appl = new Appl();
            appl.setApplSeq(new BigDecimal("6733456"));
            appl.setOutSts("14");
            List<Appl> list = new ArrayList<>();
            list.add(appl);
            Map<String, Object> map = new HashMap<>();
            Map<String, Object> map2 = new HashMap<>();
            map.put("name", "aaa");
            map.put("age", 88);
            map2.put("name", "bbb");
            map2.put("age", 88);
            map2.put("list", list);
            ObjectMessage objectMessage = new ActiveMQObjectMessage();
            objectMessage.setObject((Serializable) map2);
            //发送string数据
            queueSender.sendObjectMessage(MqConstant.TEST_METHOD_DEFAULT_QUEUE, "hahaha");
            //发送bean数据
            queueSender.sendObjectMessage(MqConstant.TEST_METHOD_OBJMSG_QUEUE, appl);
            //发送简单Map数据（value值只包含java primitive类型）
            queueSender.sendObjectMessage(MqConstant.TEST_METHOD_MAPMSG_COMPLEX_QUEUE, map);
            //发送复杂Map数据（value值可以包含对象，List等。这里必须使用ObjectMessage组装复杂类型的Map对象！）
            queueSender.sendObjectMessage(MqConstant.TEST_METHOD_MAPMSG_COMPLEX_QUEUE, objectMessage);
//            queueSender.sendObjectMessageSchedulerDelay(MqConstant.TEST_METHOD_DEFAULT_QUEUE,"delay*****************",5000L);
//            queueSender.sendObjectMessageSchedulerCron(MqConstant.TEST_METHOD_DEFAULT_QUEUE,"delay cron*****************","45 11 * 6 4");
//            queueSender.sendObjectMessageSchedulerDelay(MqConstant.TEST_METHOD_MAPMSG_COMPLEX_QUEUE, map, 10000L);
//            queueSender.sendObjectMessageSchedulerCron(MqConstant.TEST_METHOD_MAPMSG_COMPLEX_QUEUE, map, "45 11 * 6 4");
        } catch (Exception e) {
            log.error("队列发送时异常！", e);
        }
    }

    /**
     * 测试使用注解配置消费者
     */
    @Test
    public void testAnnoMq() {
        try {
            Appl appl = new Appl();
            appl.setApplSeq(new BigDecimal("6733456"));
            appl.setOutSts("14");
            List<Appl> list = new ArrayList<>();
            list.add(appl);
            Map<String, Object> map = new HashMap<>();
            map.put("name", "dsf");
            map.put("age", 88);
            map.put("list", list);
            queueSender.sendObjMessage(MqConstant.ANNOTATION_QUEUE, map);
        } catch (Exception e) {
            log.error("队列发送时异常！", e);
        }
    }

    /**
     * 测试使用jmsMessagingTemplate.convertAndSend发送primitive消息
     */
    @Test
    public void testJmsMessagingTemplatePrimitive() {
        try {
            queueSender.sendObjJms(MqConstant.TEST_METHOD_DEFAULT_QUEUE, 1);
            queueSender.sendObjJms(MqConstant.TEST_METHOD_DEFAULT_QUEUE, true);
            queueSender.sendObjJms(MqConstant.TEST_METHOD_DEFAULT_QUEUE, "***jmsMessagingTemplate***");
        } catch (Exception e) {
            log.error("队列发送时异常！", e);
        }
    }

    /**
     * 测试使用jmsMessagingTemplate.convertAndSend发送Object消息
     */
    @Test
    public void testJmsMessagingTemplateObj() {
        Appl appl = new Appl();
        appl.setApplSeq(new BigDecimal("6733456"));
        appl.setOutSts("14");
        List<Appl> list = new ArrayList<>();
        list.add(appl);
        Map<String, Object> map = new HashMap<>();
        Map<String, Object> map2 = new HashMap<>();
        map.put("name", "aaa");
        map.put("age", 88);
        map2.put("name", "bbb");
        map2.put("age", 88);
        map2.put("list", list);
        queueSender.sendObjJms(MqConstant.TEST_METHOD_DEFAULT_QUEUE2, map);
    }


    @Test
    public void testTopic() {
        try {
            topicSender.sendString(MqConstant.TEST_TOPIC, "hello world!");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testTopicAnno() {
        try {
            topicSender.sendString(MqConstant.ANNOTATION_TOPIC, "annotation topic");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}