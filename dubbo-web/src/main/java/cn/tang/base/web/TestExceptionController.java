package cn.tang.base.web;

import cn.tang.base.activemq.sender.QueueSender;
import cn.tang.base.service.IApplService;
import cn.tang.bean.JsonResponse;
import cn.tang.enumbean.RspCodeEnum;
import cn.tang.exception.BusinessException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.json.MappingJackson2JsonView;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Controller
@RequestMapping("/test")
public class TestExceptionController {

    @Autowired
    private IApplService applService;

    @Autowired
    private QueueSender queueSender;

    @RequestMapping("/testInterceptor")
    @ResponseBody
    public JsonResponse testInterceptor(HttpServletRequest request) {
        String name = request.getParameter("namge");
        log.info("拦截器接收参数：name--{}", name);
        return JsonResponse.success("OK");
    }


    /**
     * 测试全局异常Ajax请求和使用@ResponseBody注解的情况，返回code，message，data
     * 1.抛出自定义异常BusinessException
     * 2.抛出其他异常
     *
     * @param request
     * @return
     * @throws Exception
     */
    @RequestMapping("/testException")
    @ResponseBody
    public JsonResponse testException(HttpServletRequest request) throws Exception {
//        throw new BusinessException(RspCodeEnum.ERR99994);
        throw new Exception("发生Exception啦！");
    }

    /**
     * 测试全局异常非Ajax请求，请求跳转视图，在跳转页面时发生自定义异常BusinessException时的情况，
     * 跳转到自定义异常 映射 的页面。
     * @param request
     * @return
     * @throws BusinessException
     */
    @RequestMapping("/testException2")
    public String testException2(HttpServletRequest request) throws Exception {
        throw new BusinessException(RspCodeEnum.ERR99994);
    }

    /**
     * 测试全局异常在非Ajax请求，请求跳转视图发生非自定义异常时，跳转到 默认 错误页面
     * @param request
     * @return
     * @throws Exception
     */
    @RequestMapping("/testException3")
    public String testException3(HttpServletRequest request) throws Exception {
        throw new Exception("exception");
    }

    /**
     * 此方法虽然返回的是ModelAndView，但是由于new ModelAndView(new MappingJackson2JsonView())
     * 所以就可以返回json数据了！！！
     * 还有就是对于返回到前端的数据：
     * 如果data直接返一个list，前端data.data之后就直接是取到了一个array对象，
     * 然后就可以进行数组循环遍历取值了！
     * var array = data.data;
     * for (var i = 0;i<array.length;i++){
     * alert(array[i]);
     * }
     * 如果data返的是一个map，map中放入一个list，前端data.data之后就是得到了一个map对象，直接继续.list就
     * 可以取出放入map中的list了（map类型，直接.key就可以取出map中对应value值），然后就可以进行数组循环遍历取值了！
     * var array = data.data.list;
     * for (var i = 0;i<array.length;i++){
     * alert(array[i]);
     * }
     *
     * @param request
     * @return
     * @throws BusinessException
     */
    @RequestMapping("/testReturnJsonView")
    @ResponseBody
    public ModelAndView testReturnJsonView(HttpServletRequest request) throws BusinessException {
        ModelAndView jsonView = new ModelAndView(new MappingJackson2JsonView());
        jsonView.addObject("code", "0000");
        jsonView.addObject("message", "success");
        List<String> list = new ArrayList<>();
        list.add("aaa");
        list.add("bbb");
        list.add("ccc");
        Map<String, Object> map = new HashMap<>(2);
        map.put("list", list);
        jsonView.addObject("data", map);
        return jsonView;
    }

    /**
     * 测试dao整合
     *
     * @param request
     * @return
     */
    @RequestMapping("/testDao")
    @ResponseBody
    public Map<String, Object> testDao(HttpServletRequest request) {
        throw new BusinessException("exception");
//        String msg = null;
//        String applSeq = null;
//        Map<String,Object> rtnMap = new HashMap<>(8);
//        try {
//            applSeq = Objects.requireNonNull(request.getParameter("applSeq"),
//                    "require param is null!");
//        } catch (Exception e) {
//            e.printStackTrace();
//            msg = e.getMessage();
//        }
//        if (Objects.isNull(msg)) {
//            Appl appl = applService.selectByApplSeq(new BigDecimal(applSeq));
//            rtnMap.put("code", "0");
//            rtnMap.put("message", "success");
//            rtnMap.put("data", appl);
//        } else {
//            rtnMap.put("code", "-1");
//            rtnMap.put("message", msg);
//            rtnMap.put("data", new HashMap<>());
//        }
//        log.info("return params:{}",rtnMap.toString());
//        return rtnMap;
    }

}
