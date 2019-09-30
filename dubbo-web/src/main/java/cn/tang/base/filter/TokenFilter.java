
package cn.tang.base.filter;

import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 *
 *
 * @author tangwenlong
 */
public class TokenFilter extends OncePerRequestFilter {

    private static final int MAX_AGE = 30 * 24 * 60 * 60;

    private static final String[] uris = new String[]{
            "/base/basic", "/base/loginStatus"
    };

//	private static TokenGenerator tokenGenerator = null;

    //TODO  1.从配置文件中读取需要校验权限的路径
    //TODO   2.使用TokenAccountUtils获取useraccount
    //TODO   3.根据useraccount判断是否登录 或抛出异常
    @Override
    protected void doFilterInternal(final HttpServletRequest request,
                                    HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {
		/*String uri = request.getRequestURI();
		TokenUserAccount tokenUserAccount = null;
		for (String u : uris) {
			if (uri.contains(u)) {
				try {
					tokenUserAccount = TokenAccountUtils.checkLogin(request);
				} catch (Exception e) {
					noLoginReturn(response);
				}
				break;
			}
		}

		TokenAccountUtils.cacheLoginAccount(tokenUserAccount);

		SpringBeanUtil.getBean(RedisManagerForToken.class);*/

        chain.doFilter(request, response);

    }

	/*private void noLoginReturn(HttpServletResponse response) throws IOException {
		try {
			JSONObject json = new JSONObject();
			JsonError jsonError = new JsonError();
			JsonResponse jsonResponse = new JsonResponse();
			jsonResponse.setCode("1");
			jsonError.setErrcode("-1");
			jsonError.setErrmsg("noLogin");
			jsonResponse.setData(jsonError);
			json.put("code", jsonResponse.getCode());
			json.put("data", jsonResponse.getData());
			PrintWriter pw = response.getWriter();
			pw.print(json.toString());
			pw.close();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}*/
}
