package cn.tang.base.web;

import cn.tang.base.activemq.common.MqConstant;
import cn.tang.base.activemq.sender.QueueSender;
import cn.tang.base.bean.Appl;
import cn.tang.bean.JsonResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.activemq.command.ActiveMQMapMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.jms.MapMessage;
import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.*;

@Slf4j
@Controller
@RequestMapping("/testmq")
public class TestMqController {

    @Autowired
    private QueueSender queueSender;

    /**
     * 一开始没加@ResponseBody注解，前端一直报404
     *
     * @param request
     * @return
     */
    @RequestMapping("/testMqWait")
    @ResponseBody
    public JsonResponse testMq(HttpServletRequest request) {
        String name = Objects.requireNonNull(request.getParameter("name"), "name is null");
        String age = Objects.requireNonNull(request.getParameter("age"), "age is null");
        MapMessage mapMessage = new ActiveMQMapMessage();
        try {
            mapMessage.setString("name", name);
            mapMessage.setString("age", age);
            queueSender.sendMapMessageWait(MqConstant.TEST_QUEUE, mapMessage, MqConstant.TEST_QUEUE_DELAY_ONE_MINUTE);
        } catch (Exception e) {
            log.error("队列发送时异常！", e);
            return JsonResponse.fail("9999", "发送异常！");
        }
        return JsonResponse.success("发送成功！一分钟后处理！");
    }

    @RequestMapping("/testMethodDefaultMq")
    @ResponseBody
    public JsonResponse testMethodDefaultMq(HttpServletRequest request) {
        try {
            queueSender.sendString(MqConstant.TEST_METHOD_DEFAULT_QUEUE, "Hello testMethodDefaultMq!");
        } catch (Exception e) {
            log.error("队列发送时异常！", e);
            return JsonResponse.fail("9999", "发送异常！");
        }
        return JsonResponse.success("发送成功！");
    }

    @RequestMapping("/testMethodMapMsgMq")
    @ResponseBody
    public JsonResponse testMethodMapMsgMq(HttpServletRequest request) {
        try {

            MapMessage map = new ActiveMQMapMessage();
            map.setString("name", "hello");
            map.setInt("age", 33);
            map.setDouble("salary", 9999.99);
//            MapMessage的value值只能是primitive type（java基本数据类型）
//            Appl appl = new Appl();
//            appl.setApplSeq(new BigDecimal("6733456"));
//            appl.setOutSts("14");
//            map.setObject("appl", appl);
            queueSender.sendMapMessage(MqConstant.TEST_METHOD_MAPMSG_QUEUE, map);
        } catch (Exception e) {
            log.error("队列发送时异常！", e);
            return JsonResponse.fail("9999", "发送异常！");
        }
        return JsonResponse.success("发送成功！");
    }

    @RequestMapping("/testMethodObjMsgMq")
    @ResponseBody
    public JsonResponse testMethodObjMsgMq(HttpServletRequest request) {
        try {
            Appl appl = new Appl();
            appl.setApplSeq(new BigDecimal("6733456"));
            appl.setOutSts("14");
//            ObjectMessage objectMessage = new ActiveMQObjectMessage();
//            objectMessage.setObject(appl);
            queueSender.sendObjMessage(MqConstant.TEST_METHOD_OBJMSG_QUEUE, appl);
        } catch (Exception e) {
            log.error("队列发送时异常！", e);
            return JsonResponse.fail("9999", "发送异常！");
        }
        return JsonResponse.success("发送成功！");
    }

    @RequestMapping("/testMethodObjMapMsgMq")
    @ResponseBody
    public JsonResponse testMethodObjMapMsgMq(HttpServletRequest request) {
        try {
            Appl appl = new Appl();
            appl.setApplSeq(new BigDecimal("6733456"));
            appl.setOutSts("14");
            Map<String, Object> map = new HashMap<>();
            map.put("name", "dsf");
            map.put("age", 88);
            map.put("appl", appl);
            queueSender.sendObjMessage(MqConstant.TEST_METHOD_MAPMSG_COMPLEX_QUEUE, map);
        } catch (Exception e) {
            log.error("队列发送时异常！", e);
            return JsonResponse.fail("9999", "发送异常！");
        }
        return JsonResponse.success("发送成功！");
    }

    @RequestMapping("/testMethodObjMapListMsgMq")
    @ResponseBody
    public JsonResponse testMethodObjMapListMsgMq(HttpServletRequest request) {
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
            queueSender.sendObjMessage(MqConstant.TEST_METHOD_MAPMSG_COMPLEX_QUEUE, map);
        } catch (Exception e) {
            log.error("队列发送时异常！", e);
            return JsonResponse.fail("9999", "发送异常！");
        }
        return JsonResponse.success("发送成功！");
    }

}
