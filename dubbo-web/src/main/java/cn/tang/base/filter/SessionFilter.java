package cn.tang.base.filter;

import org.apache.shiro.SecurityUtils;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author tangwenlong
 */
public class SessionFilter implements Filter {

    private static final String[] urls = new String[]{
            "/base/account/manager/", "/loan/", "/loan/", "/veh/", "/base/manager/"
    };

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException,
            ServletException {
        HttpServletRequest httpServletRequest = (HttpServletRequest) request;
        HttpServletResponse httpServletResponse = (HttpServletResponse) response;

        String uri = httpServletRequest.getRequestURI();

        //if (httpServletRequest.getSession().getAttribute("user") == null) {
        if (!SecurityUtils.getSubject().isAuthenticated()) {
            //判断session里是否有用户信息
            if (httpServletRequest.getHeader("x-requested-with") != null
                    && httpServletRequest.getHeader("x-requested-with").equalsIgnoreCase("XMLHttpRequest")) {
                //如果是ajax请求响应头会有，x-requested-with
                //判断是否需要拦截
                for (String url : urls) {
                    if (uri.indexOf(url) != -1) {
                        //在响应头设置session状态
                        httpServletResponse.setHeader("sessionstatus", "timeout");
                        break;
                    }
                }
            }
        }
        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {

    }

}
