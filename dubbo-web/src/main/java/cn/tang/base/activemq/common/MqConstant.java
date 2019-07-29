package cn.tang.base.activemq.common;

public class MqConstant {
    /**
     * 测试队列
     */
    public static final String TEST_QUEUE = "dubbo.web.test.queue";
    /**
     * 默认字符串队列
     */
    public static final String TEST_METHOD_DEFAULT_QUEUE = "dubbo.web.test.method.default.queue";
    public static final String TEST_METHOD_DEFAULT_QUEUE2 = "dubbo.web.test.method.default.queue2";
    /**
     * 发送对象bean队列
     */
    public static final String TEST_METHOD_OBJMSG_QUEUE = "dubbo.web.test.method.objmsg.queue";
    /**
     * 发送简单Map队列
     */
    public static final String TEST_METHOD_MAPMSG_QUEUE = "dubbo.web.test.method.mapmsg.queue";
    /**
     * 发送复杂Map队列
     */
    public static final String TEST_METHOD_MAPMSG_COMPLEX_QUEUE = "dubbo.web.test.method.mapmsg.complex.queue";
    /**
     * 发送注解处理队列
     */
    public static final String ANNOTATION_QUEUE = "annotation.queue";
    /**
     * 发送队列延迟一分钟
     */
    public static final Long TEST_QUEUE_DELAY_ONE_MINUTE = 60000L;


    public static final String TEST_TOPIC = "dubbo.web.test.msg.topic";

    public static final String ANNOTATION_TOPIC = "annotation.topic";

}
