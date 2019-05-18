package cn.tang.base.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.annotation.Resource;

@ContextConfiguration("classpath:spring-dubbo-consumer.xml")
@RunWith(SpringJUnit4ClassRunner.class)
public class MessageImplTest {

//    @Autowired
//    @Qualifier("messageImpl")
    @Resource(name = "messageImpl")
    private IMessage message;

    @Test
    public void echo() {
        System.out.println(message.echo("*****************Hello Dubbo!!!*******************"));
    }
}