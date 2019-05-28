package cn.tang.base.redis;

import cn.tang.bean.JsonResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

/**
 * @author tangwenlong
 * @description: redis缓存处理Controller
 * @date 2019/5/28
 */
@RestController
@RequestMapping("/redis")
public class RedisController {

    @Autowired
    private RedisManager redisManager;

    @Autowired
    private RedisHandle redisHandle;

    @RequestMapping("/test")
    public JsonResponse redisTest(HttpServletRequest request) {
        String key = request.getParameter("key");
        String value = request.getParameter("value");
        String msg = redisHandle.redisTest(key, value);
        return JsonResponse.success(msg);
    }


}
