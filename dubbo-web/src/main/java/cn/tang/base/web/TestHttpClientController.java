package cn.tang.base.web;

import cn.tang.bean.JsonResponse;
import com.alibaba.fastjson.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@RequestMapping("/testHttp")
@RestController
public class TestHttpClientController {

    private static final Logger logger = LoggerFactory.getLogger(TestHttpClientController.class);

    @RequestMapping(value = "/testGet", method = RequestMethod.GET)
    public JsonResponse testGet(HttpServletRequest request) {
        String name = request.getParameter("name");
        String age = request.getParameter("age");
        logger.info("测试GET请求，name[{}],age[{}]", name, age);
        return JsonResponse.success("OK");
    }

    @RequestMapping(value = "/testPost", method = RequestMethod.POST)
    public JsonResponse testPost(@RequestBody JSONObject jsonObject) {
        logger.info("测试POST请求，params[{}]", jsonObject.toJSONString());
        return JsonResponse.success("OK");
    }
}
