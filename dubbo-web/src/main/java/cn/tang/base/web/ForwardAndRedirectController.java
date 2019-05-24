package cn.tang.base.web;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.support.RequestContextUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Map;
import java.util.Objects;

@Slf4j
@Controller
@RequestMapping("/testForwardAndRedirect")
public class ForwardAndRedirectController {


    /**
     * 使用request.getAttribute（） 的方式可以都获取到MVC  和   MAV带的参数！！！
     *
     * @param request
     * @return
     */
    @RequestMapping("/indexForward")
    public String indexForward(HttpServletRequest request) {
        String name = (String) request.getAttribute("name");
        log.info("name:{}", name);
        return "/index";
    }


    /**
     * 对于@ModelAttribute注解只有重定向的时候可以使用
     * 关于重定向的几种存取值方式
     * 1.通过session存，session取；最原始方法
     * 2.通过redirectAttributes存，@ModelAttribute取；
     * 不论是redirectAttributes.addAttribute()方式将参数拼接在url后面
     * 还是redirectAttributes.addFlashAttribute()方法设置的参数，都可以取到！！！；
     * 对于放到session中的数据，这个注解会报错，会报java.lang.NoSuchMethodException！
     * 3.通过redirectAttributes存，RequestContextUtils取；
     * 对于redirectAttributes.addAttribute()方式将参数拼接在url后面，取值为null
     * 可取到redirectAttributes.addFlashAttribute()方法设置的参数。
     * 对于放到session中的数据RequestContextUtils.getInputFlashMap(request)方法获取到的是null，调用get时NPE！
     */
    @RequestMapping("/indexRedirect")
    public String indexRedirect(@ModelAttribute("age") Integer age, HttpServletRequest request) {
        HttpSession session = request.getSession();
        Object sessionAge = session.getAttribute("age");
        session.removeAttribute("age");
        log.info("age from session:{}", sessionAge);

        log.info("age from @ModelAttribute:{}", age);

        Map<String, Object> params = (Map<String, Object>) RequestContextUtils.getInputFlashMap(request);
        if (Objects.nonNull(params)) {
            Object rcuAge = params.get("age");
            log.info("age from RequestContextUtils:{}", rcuAge);
        } else {
            log.error("RequestContextUtils.getInputFlashMap(request) 取值为空！");
        }

        return "/index";
    }

    /**
     * MVC带参数请求转发
     *
     * @param request
     * @return
     */
    @RequestMapping("/forwardMVC")
    public String forwardMVC(HttpServletRequest request) {
        request.setAttribute("name", "MVC forward");
        //不能省略forward:，否则就会解析后面的为html！
        return "forward:/testForwardAndRedirect/indexForward;";
    }

    /**
     * ModelAndView带参数请求转发
     *
     * @return
     */
    @RequestMapping("/forward")
    public ModelAndView forward() {
        //默认forward，可以不用写；但是并不行；
        //猜测是因为配置了视图解析器的原因！
        ModelAndView mav = new ModelAndView("forward:/testForwardAndRedirect/indexForward");
        mav.addObject("name", "MAV forward");
        return mav;
    }

    /**
     * 对于MVC或者MAV方式带参数重定向存值方式
     * 1.通过session存，但是取的时候需要session取
     * 2.通过RedirectAttributes存，取的时候可以使用@ModelAttribute注解或者使用RequestContextUtils工具
     */
    /**
     * MVC带参数重定向
     *
     * @return
     */
    @RequestMapping("/redirectMVC")
    public String redirectMVC(RedirectAttributes redirectAttributes, HttpServletRequest request) {
        request.getSession().setAttribute("age", 18);
//        redirectAttributes.addAttribute("age", 18);  //URL后面拼接参数
        redirectAttributes.addFlashAttribute("age", 18);
        return "redirect:/testForwardAndRedirect/indexRedirect";
        /**
         * 可重定向到其他controller
         */
//        return "redirect:/otherController/indexRedirect";
        /**
         * 会重定向到其他服务器
         */
//        return "redirect:http://www.baidu.com";
    }

    /**
     * ModelAndView带参数重定向
     *
     * @return
     */
    @RequestMapping("/redirect")
    public ModelAndView redirect(RedirectAttributes redirectAttributes, HttpServletRequest request) {
        request.getSession().setAttribute("age", 18);
        ModelAndView mav = new ModelAndView("redirect:/testForwardAndRedirect/indexRedirect");
//        redirectAttributes.addAttribute("age", 18);  //URL后面拼接参数
        redirectAttributes.addFlashAttribute("age", 18);
        return mav;
    }

    /**
     * 原生servlet方式重定向
     * 如果传参数重定向的话，一致报500.
     *
     * @return
     */
    @RequestMapping("/redirectServlet")
    public void redirectBaidu(RedirectAttributes redirectAttributes, HttpServletResponse response) throws IOException {
        redirectAttributes.addFlashAttribute("age", "18");
        /**
         * 对于下面的写法是重定向到本项目中的路径，注意与上面的区别，上面写的是
         * “redirect:/testForwardAndRedirect/indexRedirect”。
         * 是以项目根路径追加的，可以请求到本项目其他controller路径！
         * 而下面写的是
         * 1.response.sendRedirect("indexForward");
         *  解析时已经加上了本类controller的拦截路径！会重定向到正确的地址。
         * 2.response.sendRedirect("/dubbo-web/testForwardAndRedirect/indexForward");
         * 则会重定向到http://localhost:9666/testForwardAndRedirect/indexForward，没有项目根路径，错误的
         * 3.response.sendRedirect("/dubbo-web/testForwardAndRedirect/indexForward");
         *  response.sendRedirect("http://localhost:9666/dubbo-web/testForwardAndRedirect/indexForward");
         *  都会重定向到正确的地址。
         * 4.response.sendRedirect("http://www.baidu.com");
         *  会重定向到指定的服务器，关键需要加http://前缀。
         */
//        response.sendRedirect("indexForward");
//        response.sendRedirect("/testForwardAndRedirect/indexForward");
//        response.sendRedirect("/dubbo-web/testForwardAndRedirect/indexForward");
//        response.sendRedirect("http://localhost:9666/dubbo-web/testForwardAndRedirect/indexForward");
        response.sendRedirect("http://www.baidu.com");
        log.info("ddd");
    }
}
