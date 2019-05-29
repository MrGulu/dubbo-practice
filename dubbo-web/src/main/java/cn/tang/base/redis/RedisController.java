package cn.tang.base.redis;

import cn.tang.base.service.IApplService;
import cn.tang.bean.JsonResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

/**
 * @author tangwenlong
 * @description: redis缓存处理Controller
 * @date 2019/5/28
 */
@Slf4j
@RestController
@RequestMapping("/redis")
public class RedisController {

    @Autowired
    private RedisManager redisManager;

    @Autowired
    private RedisHandle redisHandle;

    @Autowired
    private IApplService applService;

    public Object set(String key, Object value, int ttl) {
        return redisManager.getCache().put(key, value, ttl);
    }

    public Boolean exists(String key) {
        return redisManager.getCache().isExits(key);
    }

    public Boolean remove(String key) {
        return redisManager.getCache().remove(key);
    }

    @SuppressWarnings("unused")
    public String get(String key) {
        Object value = redisManager.getCache().get(key);
        return value == null ? "null" : value.toString();
    }

    /*即使清除缓存时，缓存中已无该值或者改group就是empty list or set，也会返回true*/

    /**
     * @param applSeq
     * @return
     * @description 清除鉴权时缓存的applCache信息
     */
    @RequestMapping("/removeApplCache")
    @ResponseBody
    public Map<String, Object> removeApplCache(@RequestParam("applSeq") String applSeq) {
        log.info("清除redis缓存ApplCache补偿接口start……业务号：" + applSeq);
        Map<String, Object> map = new HashMap<>(8);
        applService.removeRedisApplCache(applSeq);
        log.info("清除redis缓存ApplCache补偿接口end……业务号：" + applSeq);
        map.put("status", "OK");
        return map;
    }

    /**
     * @param key 清除redis指定key值缓存
     * @return
     * @description
     */
    @RequestMapping("/removeRedisKey")
    @ResponseBody
    public JsonResponse removeRedisKey(@RequestParam("key") String key) {
        log.info("清除redis缓存指定key值：{}", key);
        if (redisManager.getCache().remove(key)) {
            log.info("清除redis缓存指定key值：{}成功！", key);
            return JsonResponse.success("清除redis缓存指定key值：" + key + "成功！");
        } else {
            return JsonResponse.fail("9999", "清除失败！");
        }
    }

    /**
     * @return
     * @description 清除redis初始化时group全部缓存
     */
    @RequestMapping("/removeRedisGroup")
    @ResponseBody
    public JsonResponse removeRedisGroup() {
        String group = redisManager.getCache().getGroup();
        log.info("清除redis缓存group:{}", group);
        if (redisManager.getCache().clear()) {
            log.info("清除redis缓存group:{}成功！", group);
            return JsonResponse.success("清除redis缓存group：" + group + "成功！");
        } else {
            return JsonResponse.fail("9999", "清除失败！");
        }
    }


    @RequestMapping("/test")
    public JsonResponse redisTest(HttpServletRequest request) {
        String key = request.getParameter("key");
        String value = request.getParameter("value");
        String msg = redisHandle.redisTest(key, value);
        return JsonResponse.success(msg);
    }


}
