package cn.tang.base;

import cn.tang.base.service.IMessage;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.annotation.Resource;

@ContextConfiguration("classpath:spring-dubbo-consumer.xml")
@RunWith(SpringJUnit4ClassRunner.class)
public class DubboServiceTest {

//    @Autowired
//    @Qualifier("messageImpl")
    @Resource(name = "messageImpl")
    private IMessage message;

    @Test
    public void test1() {
        System.out.println(message.echo("*****************Hello Dubbo!!!*******************"));
    }
}
