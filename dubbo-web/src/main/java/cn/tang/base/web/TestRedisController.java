package cn.tang.base.web;

import cn.tang.base.redis.RedisController;
import cn.tang.bean.JsonResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Objects;

@RequestMapping("/redis")
@RestController
public class TestRedisController {

    public static final Logger logger = LoggerFactory.getLogger(TestRedisController.class);

    @Autowired
    private RedisController redisController;

    @RequestMapping("setString")
    public JsonResponse setString(@RequestParam("key") String key, @RequestParam("value") String value) {
        logger.info("setString method receieve param:[{}]=[{}]", key, value);
        Object set = redisController.set(key, value, 600);
        if (Objects.isNull(set)) {
            logger.error("setString error");
            return JsonResponse.fail("-1111", "error");
        }
        if (Objects.nonNull(value) && value.equals(set)) {
            logger.info("setString success");
            return JsonResponse.success("OK");
        }
        return JsonResponse.success("OK");
    }

    @RequestMapping("get")
    public JsonResponse get(@RequestParam("key") String key) {
        logger.info("get method receieve param:[{}]", key);
        String s = redisController.get(key);
        logger.info("get redis value:[{}]", s);
        return JsonResponse.success("OK", s);
    }
}
