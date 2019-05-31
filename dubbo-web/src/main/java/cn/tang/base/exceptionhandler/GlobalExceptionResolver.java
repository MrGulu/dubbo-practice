package cn.tang.base.exceptionhandler;

import cn.tang.exception.BusinessException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.method.annotation.ExceptionHandlerExceptionResolver;
import org.springframework.web.servlet.view.json.MappingJackson2JsonView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Method;
import java.net.URLEncoder;
import java.util.*;

/**
 * @author : tangwenlong
 * @description: 全局异常处理
 */
@Slf4j
public class GlobalExceptionResolver extends ExceptionHandlerExceptionResolver {

    private String defaultErrorView;
    private Properties exceptionMappings;

    public String getDefaultErrorView() {
        return defaultErrorView;
    }

    public void setDefaultErrorView(String defaultErrorView) {
        this.defaultErrorView = defaultErrorView;
    }

    public Properties getExceptionMappings() {
        return exceptionMappings;
    }

    public void setExceptionMappings(Properties exceptionMappings) {
        this.exceptionMappings = exceptionMappings;
    }

    @Override
    protected ModelAndView doResolveHandlerMethodException(HttpServletRequest request,
                                                           HttpServletResponse response, HandlerMethod handlerMethod, Exception exception) {
        if (handlerMethod == null) {
            return null;
        }
        /*当发生异常时，发生异常的方法会绑定到handlerMethod，进而可以取到Method对象，
         然后下面通过AnnotationUtils查找这个方法中是否包含@ResponseBody注解
        */
        Method method = handlerMethod.getMethod();
        if (method == null) {
            return null;
        }
        //目前发现下面方法调用一直返回null
        ModelAndView returnValue = super.doResolveHandlerMethodException(request, response, handlerMethod, exception);
        /**
         * 如果抛出异常的方法使用@ResponseBody注解或者该请求是 AJAX 异步HTTP请求，
         * 再判断是否是自定义异常BusinessException进行返回code，message和data
         */
        ResponseBody responseBodyAnn = AnnotationUtils.findAnnotation(method, ResponseBody.class);
//        在判断Ajax时，也可以使用下面两种方法判断true或者!=null
//        boolean hasMethodAnnotation = handlerMethod.hasMethodAnnotation(ResponseBody.class);
//        ResponseBody methodAnnotation = handlerMethod.getMethodAnnotation(ResponseBody.class);
        try {
            if (("XMLHttpRequest").equals(request.getHeader("X-Requested-With")) || (responseBodyAnn != null)) {
                log.info("全局异常处理：Ajax请求或@ResponseBody……");
                ModelAndView jsonView = new ModelAndView(new MappingJackson2JsonView());
                if (exception instanceof BusinessException) {
                    log.error("全局异常Ajax或@ResponseBody请求自定义异常！", exception);
                    BusinessException businessException = (BusinessException) exception;
                    jsonView.addObject("code", businessException.getErrorCode());
                    jsonView.addObject("message", businessException.getMessage());
                    jsonView.addObject("data", new HashMap<>());
                    log.error("全局异常处理：Ajax请求或@ResponseBody自定义异常params：code：{}，message：{}", businessException.getErrorCode(), businessException.getMessage());
                } else {
                    log.error("全局异常Ajax请求或@ResponseBody非自定义异常！", exception);
                    String message = StringUtils.isEmpty(exception.getMessage()) ? "系统异常！" : exception.getMessage();
                    jsonView.addObject("code", "99999");
                    jsonView.addObject("message", message);
                    jsonView.addObject("data", new HashMap<>());
                    log.error("全局异常处理：Ajax请求或@ResponseBody非自定义异常params：code：{}，message：{}", "99999", message);
                }
                response.setStatus(200);
                return jsonView;
            }
        } catch (Exception e) {
            log.error("全局异常Ajax请求或@ResponseBodycatch捕获:" + Arrays.toString(e.getStackTrace()));
            response.setStatus(200);
            ModelAndView jsonView = new ModelAndView(new MappingJackson2JsonView());
            jsonView.addObject("code", "99998");
            jsonView.addObject("message", "系统异常");
            jsonView.addObject("data", new HashMap<Object, Object>());
            return jsonView;
        }
        /**
         * 如果上述条件不满足，先判断是否配置了异常映射（在spring-mvc.xml中配置），
         * 也就是发生了这个异常BusinessException之后，跳转到指定页面
         */
        if (this.exceptionMappings != null) {
            Set<Map.Entry<Object, Object>> eSet = this.exceptionMappings.entrySet();
            for (Map.Entry entry : eSet) {
                //exception.getClass().getName()得到的是异常类的全路径名，所以在配置的时候也要配置对应异常类的全路径名
                if (exception.getClass().getName().equals(entry.getKey())) {
                    Map<String, Object> param = new HashMap<>();
                    String errorMsg = exception.getMessage();
                    if (StringUtils.isEmpty(errorMsg)) {
                        errorMsg = "系统异常！";
                    }
                    try {
                        param.put("errorMsg", URLEncoder.encode(errorMsg, "UTF-8"));
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                    //对于两个参数以上的日志打印，使用Object[]数组的方式
                    log.error("全局异常处理：跳转自定义异常映射视图:{},异常：{},errorMsg:{}", new Object[]{entry.getValue(), entry.getKey(), errorMsg});
                    //跳转到配置的异常映射路径，并且携带参数param
                    returnValue = new ModelAndView(entry.getValue().toString(), param);
                    break;
                }
            }
        }
        /**
         * 如果前面两种条件都不满足，即不满足：
         * 1.@ResponseBody注解或AJAX请求
         * 2.在跳转页面时发生自定义异常BusinessException
         * 时，下面跳转到设置的默认错误页面（spring-mvc.xml中设置）
         */
        if (returnValue == null) {
            Map<String, Object> param = new HashMap<>();
            try {
                param.put("errorMsg", URLEncoder.encode("系统异常！", "UTF-8"));
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            response.reset();
            log.info("全局异常处理：跳转默认视图:{},param:{}", this.defaultErrorView, "系统异常！");
            returnValue = new ModelAndView(this.defaultErrorView, param);
        }
        return returnValue;
    }
}
