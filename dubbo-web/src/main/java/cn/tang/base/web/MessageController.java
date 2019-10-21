package cn.tang.base.web;

import cn.tang.base.bean.Student;
import cn.tang.base.service.IMessageService;
import cn.tang.base.service.StudentService;
import cn.tang.bean.JsonResponse;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Controller
@RequestMapping("/testMsg")
public class MessageController {

    public static final Logger logger = LoggerFactory.getLogger(MessageController.class);

    @Autowired
    @Qualifier("messageServiceImpl")
//    @Resource(name = "messageServiceImpl")
    private IMessageService messageService;

    @Resource
    private StudentService studentService;

    @RequestMapping("/hello")
    public ModelAndView hello() {
        log.info("进入处理程序…………");
        ModelAndView mav = new ModelAndView("/base/DubboBase");
        mav.addObject("msg", messageService.echo("Hello World!"));
        return mav;
    }

    @RequestMapping("/returnMsg")
    @ResponseBody
    public Map returnMsg() {
        log.debug("debug");
        log.info("进入处理程序…………");
        log.error("error");
        Map<String,Object> map = new HashMap<>(4);
        map.put("msg", messageService.echo("Hello World!"));
        logger.debug("logger debug");
        logger.info("logger info");
        logger.error("logger error");
        return map;
    }

    @RequestMapping("/get")
    @ResponseBody
    public JsonResponse getStudent(@RequestParam("id") Integer id) {
        logger.info("getStudent method receieve param:[{}]", id);
        Student student = studentService.getStudentById(id);
        logger.info("查询学生信息：" + student.toString());
        return JsonResponse.success("OK", student);
    }
}
