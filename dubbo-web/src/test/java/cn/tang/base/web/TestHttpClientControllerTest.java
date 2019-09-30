package cn.tang.base.web;

import cn.tang.base.bean.Appl;
import cn.tang.utils.HttpClientUtils;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TestHttpClientControllerTest {

    private static final Logger logger = LoggerFactory.getLogger(TestHttpClientControllerTest.class);

    @Test
    public void testGet() {
        String url = "http://localhost/dubbo-web/testHttp/testGet?name=tang&age=18";
        String result = HttpClientUtils.httpGet(url);
        logger.info("receieved params:[{}]", result);
        JSONObject jsonObject = JSON.parseObject(result);
        String code = jsonObject.getString("code");
        String message = jsonObject.getString("message");
        if ("0".equals(code)) {
            logger.info("测试get请求成功！code[{}],message[{}]", code, message);
        } else {
            logger.error("测试get请求失败！code[{}],message[{}]", code, message);
        }
    }

    @Test
    public void testPost() {
        String url = "http://localhost/dubbo-web/testHttp/testPost";
        JSONObject jsonObject = buildPostParams();
        logger.info("send params:[{}]", jsonObject.toJSONString());
        String result = HttpClientUtils.httpJsonPost(url, jsonObject.toJSONString());
        logger.info("receieved params:[{}]", result);
        jsonObject = JSON.parseObject(result);
        String code = jsonObject.getString("code");
        String message = jsonObject.getString("message");
        if ("0".equals(code)) {
            logger.info("测试post请求成功！code[{}],message[{}]", code, message);
        } else {
            logger.error("测试post请求失败！code[{}],message[{}]", code, message);
        }
    }

    private static JSONObject buildPostParams() {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("key", "haha");
        jsonObject.put("value", "xixi");
        List<Object> list = new ArrayList<>();
        list.add("a");
        Appl appl = new Appl();
        appl.setApplSeq(new BigDecimal("232323"));
        appl.setCustName("tang");
        appl.setOutSts("35");
        list.add(appl);
        Map<String, Object> map = new HashMap<>();
        map.put("ceshi", 111);
        map.put("post", "dsjflsd");
        list.add(map);
        jsonObject.put("list", list);
        return jsonObject;
    }
}