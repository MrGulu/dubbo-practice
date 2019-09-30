
package cn.tang.base.interceptor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Iterator;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @description: XSS攻击拦截器
 * @author tangwenlong
 */
public class XSSInterceptor extends HandlerInterceptorAdapter {

    private static final Logger logger = LoggerFactory.getLogger(XSSInterceptor.class);

    private static Pattern[] patterns = {
            Pattern.compile("<script>(.*?)</script>", Pattern.CASE_INSENSITIVE),
            Pattern.compile("src[\r\n]*=[\r\n]*\\'(.*?)\\'", Pattern.CASE_INSENSITIVE & Pattern.UNICODE_CASE),
            Pattern.compile("src[\r\n]*=[\r\n]*\\\"(.*?)\\\"", Pattern.CASE_INSENSITIVE & Pattern.UNICODE_CASE),
            Pattern.compile("</script>", Pattern.CASE_INSENSITIVE),
            Pattern.compile("<script(.*?)>", Pattern.CASE_INSENSITIVE & Pattern.UNICODE_CASE),
            Pattern.compile("eval\\((.*?)\\)", Pattern.CASE_INSENSITIVE & Pattern.UNICODE_CASE),
            Pattern.compile("expression\\((.*?)\\)", Pattern.CASE_INSENSITIVE & Pattern.UNICODE_CASE),
            Pattern.compile("javascript:", Pattern.CASE_INSENSITIVE),
            Pattern.compile("vbscript:", Pattern.CASE_INSENSITIVE),
            Pattern.compile("onload(.*?)=", Pattern.CASE_INSENSITIVE & Pattern.UNICODE_CASE)};

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {
        System.out.println("XSSInterceptor preHandle***************");
        logger.debug("xss攻击防御-处理http请求-开始");
        Map parameterMap = request.getParameterMap();
        Iterator keyIt = parameterMap.keySet().iterator();
        while (keyIt.hasNext()) {
            String key = (String) keyIt.next();
            String[] values = request.getParameterValues(key);
            for (String value : values) {
                logger.debug("value:{}", value);
                if (!isValid(value)) {
                    logger.error("疑似xss攻击，已被拦截。URL:{}；参数:{}={}", new Object[]{request.getRequestURL(), key, value});
                    throw new IllegalArgumentException("疑似xss攻击，已被拦截");
                }
            }
        }
        logger.debug("xss攻击防御-处理http请求-结束");
        return true;
    }

    private static boolean isValid(String value) {
        if ((value != null) &&
                (value != null)) {
            value = value.replaceAll("\\s", "");
            for (Pattern scriptPattern : patterns) {
                Matcher matcher = scriptPattern.matcher(value);
                if (matcher.matches()) {
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        System.out.println("XSSInterceptor postHandle*************");
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        System.out.println("XSSInterceptor afterCompletion***************");
    }

    @Override
    public void afterConcurrentHandlingStarted(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        super.afterConcurrentHandlingStarted(request, response, handler);
    }
}
