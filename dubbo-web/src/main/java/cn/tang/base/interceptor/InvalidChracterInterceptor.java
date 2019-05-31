package cn.tang.base.interceptor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Iterator;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author tangwenlong
 * @description: 无效字符拦截器
 */
public class InvalidChracterInterceptor extends HandlerInterceptorAdapter {

    private static final Logger logger = LoggerFactory.getLogger(InvalidChracterInterceptor.class);
    /**
     * 在下面Pattern.compile()方法第二个参数中，如果在启用了UNICODE_CASE标志的基础上，
     * 如果你还启用了CASE_INSENSITIVE标志，那么它会对Unicode字符进行大小写不明感的匹配。
     * 也就是Pattern.CASE_INSENSITIVE & Pattern.UNICODE_CASE
     * 默认情况下，大小写不敏感的匹配只适用于US-ASCII字符集
     */
    private static Pattern[] patterns = {
            Pattern.compile("undefined", Pattern.CASE_INSENSITIVE & Pattern.UNICODE_CASE),
            Pattern.compile("null", Pattern.CASE_INSENSITIVE & Pattern.UNICODE_CASE)
    };

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        logger.debug("无效字符拦截-处理http请求-开始");
        Map parameterMap = request.getParameterMap();
        Iterator keyIt = parameterMap.keySet().iterator();
        while (keyIt.hasNext()) {
            String key = (String) keyIt.next();
            String[] values = request.getParameterValues(key);
            for (String value :
                    values) {
                logger.debug("values:{}", value);
                if (!isValid(value)) {
                    logger.error("疑似无效字符传参，已被拦截。URL:{}；参数:{}={}", new Object[]{request.getRequestURL(), key, value});
                    throw new IllegalArgumentException("疑似无效字符传参，已被拦截");
                }
            }
        }
        return super.preHandle(request, response, handler);
    }

    private static boolean isValid(String value) {
        if (value != null) {
            for (Pattern scriptPattern :
                    patterns) {
                Matcher matcher = scriptPattern.matcher(value);
                if (matcher.matches()) {
                    return false;
                }
            }
        }
        return true;
    }
}
