package cn.tang.base.web;

import cn.tang.base.activemq.common.MqConstant;
import cn.tang.base.activemq.sender.QueueSender;
import cn.tang.base.bean.Appl;
import cn.tang.base.service.IApplService;
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
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Slf4j
@Controller
@RequestMapping("/test")
public class TestController {

    @Autowired
    private IApplService applService;

    @Autowired
    private QueueSender queueSender;

    @RequestMapping("/testDao")
    @ResponseBody
    public Map<String, Object> testDao(HttpServletRequest request) {
        String msg = null;
        String applSeq = null;
        Map<String,Object> rtnMap = new HashMap<>(8);
        try {
            applSeq = Objects.requireNonNull(request.getParameter("applSeq"),
                    "require param is null!");
        } catch (Exception e) {
            e.printStackTrace();
            msg = e.getMessage();
        }
        if (Objects.isNull(msg)) {
            Appl appl = applService.selectByApplSeq(new BigDecimal(applSeq));
            rtnMap.put("code", "0");
            rtnMap.put("message", "success");
            rtnMap.put("data", appl);
        } else {
            rtnMap.put("code", "-1");
            rtnMap.put("message", msg);
            rtnMap.put("data", new HashMap<>());
        }
        log.info("return params:{}",rtnMap.toString());
        return rtnMap;
    }

    /**
     * 一开始没加@ResponseBody注解，前端一直报404
     *
     * @param request
     * @return
     */
    @RequestMapping("/testMq")
    @ResponseBody
    public JsonResponse testMq(HttpServletRequest request) {
        String name = Objects.requireNonNull(request.getParameter("name"), "name is null");
        String age = Objects.requireNonNull(request.getParameter("age"), "age is null");
        MapMessage mapMessage = new ActiveMQMapMessage();
        try {
            mapMessage.setString("name", name);
            mapMessage.setString("age", age);
            queueSender.sendMapMessage(MqConstant.TEST_QUEUE, mapMessage);
//            queueSender.sendMapMessageWait(MqConstant.TEST_QUEUE,mapMessage,MqConstant.TEST_QUEUE_DELAY);
        } catch (Exception e) {
            log.error("队列发送时异常！", e);
        }
        return JsonResponse.success("发送成功！一分钟后处理！");
    }

}
