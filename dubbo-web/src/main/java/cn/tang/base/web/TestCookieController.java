package cn.tang.base.web;

import cn.tang.base.bean.Appl;
import cn.tang.bean.JsonResponse;
import cn.tang.constant.Constant;
import cn.tang.utils.CookieUtils;
import cn.tang.utils.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.math.BigDecimal;
import java.util.Map;
import java.util.Objects;

/**
 * cookie相关说明：
 * <p>
 * 1、cookie由服务器端创建，然后添加到HttpServletResponse中发送给客户端（浏览器）。
 * <p>
 * 2、可以添加多个cookie键值对。
 * <p>
 * 3、cookie由键值名和键值组成。“相同domain和path”中的键值名不能重复，添加键值名重名的键值对会覆盖上一个同名的键值对。
 * <p>
 * 4、添加cookie时要指定cookie所在域（setPath），指定存在时长（setMaxAge）。
 * <p>
 * 4、服务端创建好cookie后提交给客户端，之后浏览器的每次请求（HttpServletRequest）里都会携带“cookie数组”。
 * <p>
 * 5、springmvc有两种方式获取：（1）在控制器中通过注解@CookieValue（键值名），获取指定某个cookie。（2）通过HttpServletRequest中的getcookies方法获取cookie数组，然后迭代里面的每一个cookie键值对。
 * <p>
 * <p>
 * <p>
 * session相关说明：
 * <p>
 * 1、服务器会根据客户端的请求（HttpServletRequest）创建session（request.getSession()）。
 * <p>
 * 2、每一个session都有一个唯一的标示“sessionID”，可通过.getId()获得。
 * <p>
 * 3、session是存储在服务器端的，每一个session都有一个id，当创建一个session后，会将该sessionID存放到此次访问的cookie中，当下次客户端的访问到来需要提取服务器中的session时，会根据访问中cookie里的sessionID值来找到服务器中的具体session。
 * <p>
 * 4、服务器会把长时间没有活动的Session从服务器内存中清除，此时Session便失效。Tomcat中Session的默认失效时间为20分钟。
 * <p>
 * 5、访问html等静态资源时不会创建session
 * <p>
 * Cookie的setPath()方法，指定的是可访问该Cookie的目录！默认只有同一个Web服务器上同一个路径下设置了该cookie的网页读取！如果你想让这个cookie在多个页面中共享，那么需要使用Cookie的setPath()方法设置path
 * 　　cookie.setPath("/")，在Tomcat的webapps目录下的所有目录中共享这个cookie。
 * 　　cookie.setPath("/app1")，只能在app1应用下获取这个cookie，就算这个cookie是app2产生的，app2也不能访问它。
 * 　　cookie.setPath("/app1/action")，只能在app1/action目录下获取这个cookie。
 * 　　我想你应该可以明白了，cookie的路径指的是可以访问该cookie的顶层目录，该路径的子路径也可以访问该cookie。
 * <p>
 * 　　在www.baidu.com中设置的cookie，但需要在zhidao.baidu.com下获取，这样就需要Cookie的setDomain()方法了。
 * 　　cookie.setPath("/");
 * 　　cookie.setDomain("baidu.com");//只给出域名的相同部分
 * 　　response.add(cookie);
 * <p>
 * localhost 下的cookie
 * 2018年01月15日 17:04:55 五年达尔文 阅读数 1487
 * 版权声明：本文为博主原创文章，未经博主允许不得转载。 https://blog.csdn.net/weixin_40648117/article/details/79066550
 * cookie.setPath("/")
 * <p>
 * 此时在部署在同一个服务器下的所有应用都能拿到该cookie，因为/代表webapp的根路径，及服务器的路径，所以服务器下所有应用都可以使用，要想其他应用拿不到应该
 * <p>
 * cookie.setPath("/ssm")设置相应项目名，这样就只有在该项目夏才能用这个cookie
 * <p>
 * cooike的domain默认null，除非你设置，setDomian(".xxxx")，网上说必须.开头，有待验证
 * <p>
 * when working on localhost the cookie-domain must be set to "" or NULL or FALSE
 * localhost上的工作时，cookie的域必须设置为“”或NULL或FALSE
 * <p>
 * 变态的说：
 * domain 是域，不是域名
 * localhost 是域名，不是域
 */

/**
 * Cookie的setPath()方法，指定的是可访问该Cookie的目录！默认只有同一个Web服务器上同一个路径下设置了该cookie的网页读取！如果你想让这个cookie在多个页面中共享，那么需要使用Cookie的setPath()方法设置path
 * 　　cookie.setPath("/")，在Tomcat的webapps目录下的所有目录中共享这个cookie。
 * 　　cookie.setPath("/app1")，只能在app1应用下获取这个cookie，就算这个cookie是app2产生的，app2也不能访问它。
 * 　　cookie.setPath("/app1/action")，只能在app1/action目录下获取这个cookie。
 * 　　我想你应该可以明白了，cookie的路径指的是可以访问该cookie的顶层目录，该路径的子路径也可以访问该cookie。
 *
 * 　　在www.baidu.com中设置的cookie，但需要在zhidao.baidu.com下获取，这样就需要Cookie的setDomain()方法了。
 * 　　cookie.setPath("/");
 * 　　cookie.setDomain("baidu.com");//只给出域名的相同部分
 * 　　response.add(cookie);
 */

/**
 * localhost 下的cookie
 * 2018年01月15日 17:04:55 五年达尔文 阅读数 1487
 *  版权声明：本文为博主原创文章，未经博主允许不得转载。 https://blog.csdn.net/weixin_40648117/article/details/79066550
 * cookie.setPath("/")
 *
 * 此时在部署在同一个服务器下的所有应用都能拿到该cookie，因为/代表webapp的根路径，及服务器的路径，所以服务器下所有应用都可以使用，要想其他应用拿不到应该
 *
 * cookie.setPath("/ssm")设置相应项目名，这样就只有在该项目夏才能用这个cookie
 *
 * cooike的domain默认null，除非你设置，setDomian(".xxxx")，网上说必须.开头，有待验证
 */

/**
 * when working on localhost the cookie-domain must be set to "" or NULL or FALSE
 * localhost上的工作时，cookie的域必须设置为“”或NULL或FALSE
 *
 * 变态的说：
 * domain 是域，不是域名
 * localhost 是域名，不是域
 */

/**
 * Cookie在创建的时候,会根据当前的Servlet的相对路径来设置自己的路径
 *          Servlet的相对路径(url-pattern最后的/前面的路径)
 *          比如Servlet的url-pattern为/cookie/login,相对路径:/cookie/
 *    问题: 此时,只有在访问路径为/cookie/下面的资源的时候,才会将该Cookie发送到服务器
 *    解决方案:设置Cookie的路径
 *         void setPath(String uri)  
 *        Cookie对象.setPath(“/”);表示当前应用中的所有的资源都能够共享该Cookie信息
 */
@Slf4j
@Controller
@RequestMapping("/cookie")
public class TestCookieController {

    @RequestMapping("/index")
    @ResponseBody
    public JsonResponse cookieIndex(HttpServletRequest request) {
        //请求URI，如/dubbo-web/cookie/index
        log.info(request.getRequestURI());
        //请求URL，如http://localhost:9666/dubbo-web/cookie/index
        log.info(request.getRequestURL().toString());
        log.info(request.getMethod());
        log.info(request.getAuthType());
        //项目名，如/dubbo-web
        log.info(request.getContextPath());
        log.info(request.getPathInfo());
        log.info(request.getPathTranslated());
        log.info(request.getRemoteUser());
        log.info(request.getRequestedSessionId());
        //请求路径，如/cookie/index
        log.info(request.getServletPath());

        /**
         * 设置session
         */
        log.info(request.getSession().getId());
        if (request.getSession().getAttribute("user") != null) {
            log.info(request.getSession().getAttribute("user").toString());
        } else {
            Appl appl = new Appl();
            appl.setApplSeq(new BigDecimal("6733456"));
            appl.setOutSts("14");
            request.getSession().setAttribute("user", appl);
            return JsonResponse.success("Hello", appl);
        }
        return JsonResponse.success("Hello");
    }

    @RequestMapping("/annotation")
    @ResponseBody
    public JsonResponse annotation(@CookieValue(name = "userName", required = false) String userName,
                                   @CookieValue(name = "pwd", required = false) String pwd) {
        log.info("userName=[{}],pwd=[{}]", userName, pwd);
        return JsonResponse.success("Hello");
    }

//    @RequestMapping("/add")
//    @ResponseBody
//    public JsonResponse addCookies(HttpServletRequest request, HttpServletResponse response) {
//        String userName = null, pwd = null;
//        Cookie[] cookies = request.getCookies();
//        if (Objects.nonNull(cookies)) {
//            for (int i = 0, n = cookies.length; i < n; i++) {
//                if (Constant.USER_NAME.equals(cookies[i].getName())) {
//                    userName = cookies[i].getValue();
//                } else if (Constant.USER_PWD.equals(cookies[i].getName())) {
//                    pwd = cookies[i].getValue();
//                }
//            }
//        }
//        if (Objects.isNull(userName) && Objects.isNull(pwd)) {
//            userName = request.getParameter("userName");
//            pwd = request.getParameter("pwd");
//            Cookie nameCookie = new Cookie("userName",userName);
//            Cookie pwdCookie = new Cookie("pwd",pwd);
//            nameCookie.setMaxAge(60*60*24*10);
//            nameCookie.setPath("/dubbo-web");
//            pwdCookie.setMaxAge(60*60*24*10);
//            pwdCookie.setPath("/dubbo-web");
//            response.addCookie(nameCookie);
//            response.addCookie(pwdCookie);
//        }
//        return JsonResponse.success("OK");
//    }

    @RequestMapping("/add")
    @ResponseBody
    public JsonResponse addCookies(HttpServletRequest request, HttpServletResponse response) {
        Map<String, String> cookies = CookieUtils.getCookies(request, Constant.USER_NAME, Constant.USER_PWD);
        String userName = cookies.get("userName");
        String pwd = cookies.get("pwd");
        if (StringUtils.isEmpty(userName) && StringUtils.isEmpty(pwd)) {
            userName = request.getParameter("userName");
            pwd = request.getParameter("pwd");
            CookieUtils.setCookie(response, "userName", userName, ".test.com", request.getContextPath(), 60 * 60 * 24 * 10);
            CookieUtils.setCookie(response, "pwd", pwd, ".test.com", request.getContextPath(), 60 * 60 * 24 * 10);
        }
        return JsonResponse.success("OK");
    }

    @RequestMapping("remove")
    public void removeCookies(HttpServletRequest request, HttpServletResponse response) {
        Cookie[] cookies = request.getCookies();
        if (Objects.nonNull(cookies)) {
            for (int i = 0, n = cookies.length; i < n; i++) {
                if (Constant.USER_NAME.equals(cookies[i].getName())) {
                    //如果找到同名cookie，就将value设置为null，将存活时间设置为0，再替换掉原cookie，这样就相当于删除了。
                    Cookie cookie = new Cookie(Constant.USER_NAME, null);
                    cookie.setDomain(".test.com");
                    cookie.setPath("/dubbo-web");
                    cookie.setMaxAge(0);
                    response.addCookie(cookie);
                } else if (Constant.USER_PWD.equals(cookies[i].getName())) {
                    Cookie cookie = new Cookie(Constant.USER_PWD, null);
                    cookie.setDomain(".test.com");
                    cookie.setPath("/dubbo-web");
                    cookie.setMaxAge(0);
                    response.addCookie(cookie);
                }
            }
        }
    }
}
