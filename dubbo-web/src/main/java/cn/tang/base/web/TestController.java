package cn.tang.base.web;

import cn.tang.base.bean.APPL;
import cn.tang.base.service.IApplService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

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
            APPL appl = applService.selectByApplSeq(new BigDecimal(applSeq));
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

}
