package cn.tang.utils;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class CookieUtils {
    /**
     * 获取指定cookie-单个
     *
     * @param request
     * @param cookieName
     * @return
     */
    public static String getCookie(HttpServletRequest request, String cookieName) {
        Cookie[] cookies = request.getCookies();
        if (Objects.nonNull(cookies)) {
            for (int i = 0, n = cookies.length; i < n; i++) {
                if (cookieName.equals(cookies[i].getName())) {
                    return cookies[i].getValue();
                }
            }
        }
        return null;
    }

    /**
     * 获取指定cookie-多个
     *
     * @param request
     * @param cookieNames
     * @return
     */
    public static Map<String, String> getCookies(HttpServletRequest request, String... cookieNames) {
        Map<String, String> cookieValues = new HashMap<>(16);
        Cookie[] cookies = request.getCookies();
        if (Objects.nonNull(cookies)) {
            for (int i = 0, n = cookies.length; i < n; i++) {
                for (int j = 0, m = cookieNames.length; j < m; j++) {
                    if (cookieNames[j].equals(cookies[i].getName())) {
                        cookieValues.put(cookieNames[j], cookies[i].getValue());
                    }
                }
            }
        }
        return cookieValues;
    }

    /**
     * set cookie with default
     *
     * @param response
     * @param cookieName
     * @param cookieValue
     */
    public static void setCookie(HttpServletRequest request, HttpServletResponse response, String cookieName, String cookieValue) {
        Cookie cookie = new Cookie(cookieName, cookieValue);
        cookie.setPath(request.getContextPath());
        response.addCookie(cookie);
    }

    /**
     * set cookie with path and maxAge
     *
     * @param response
     * @param cookieName
     * @param cookieValue
     * @param path
     * @param maxAge
     */
    public static void setCookie(HttpServletResponse response, String cookieName, String cookieValue, String path, int maxAge) {
        Cookie cookie = new Cookie(cookieName, cookieValue);
        cookie.setPath(path);
        cookie.setMaxAge(maxAge);
        response.addCookie(cookie);
    }

    /**
     * set cookie with domain,path and maxAge
     *
     * @param response
     * @param cookieName
     * @param cookieValue
     * @param domain
     * @param path
     * @param maxAge
     */
    public static void setCookie(HttpServletResponse response, String cookieName, String cookieValue, String domain, String path, int maxAge) {
        Cookie cookie = new Cookie(cookieName, cookieValue);
        cookie.setDomain(domain);
        cookie.setPath(path);
        cookie.setMaxAge(maxAge);
        response.addCookie(cookie);
    }
}
