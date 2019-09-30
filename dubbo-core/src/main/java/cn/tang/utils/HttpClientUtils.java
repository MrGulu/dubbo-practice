package cn.tang.utils;

import cn.tang.enumbean.RspCodeEnum;
import cn.tang.exception.BusinessException;
import org.apache.commons.io.Charsets;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * @author tangwenlong
 * @description: HttpClient工具类
 */
public class HttpClientUtils {

    private static final Logger logger = LoggerFactory.getLogger(HttpClientUtils.class);

    /**
     * @param url url
     * @return 返回String串
     * @description: 发送GET请求
     * @author: tangwenlong
     */
    public static String httpGet(String url) {
        StringUtils.requireNonEmpty(url, "必传参数url为空！");
        String result;
        HttpGet httpGet = new HttpGet(url);
        try {
            try (CloseableHttpClient client = HttpClients.createDefault();
                 CloseableHttpResponse response = client.execute(httpGet)) {
                if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                    result = EntityUtils.toString(response.getEntity());
                } else {
                    throw new BusinessException(RspCodeEnum.ERR99992, "GET", url);
                }
            }
            return result;
        } catch (IOException e) {
            logger.error("url[" + url + "]发送GET请求异常！", e);
            throw new BusinessException(RspCodeEnum.ERR99991, "GET", url);
        }
    }

    /**
     * @param url    url
     * @param params json格式字符串参数
     * @return 返回String串
     * @description: 发送POST请求
     * @author: tangwenlong
     */
    public static String httpJsonPost(String url, String params) {
        StringUtils.requireNonEmpty(url, "必传参数url为空！");
        String result;
        HttpPost httpPost = new HttpPost(url);
        StringEntity entity = new StringEntity(params, Charsets.UTF_8);
        entity.setContentType("application/json;charset=UTF-8");
        httpPost.setEntity(entity);
        try {
            try (CloseableHttpClient client = HttpClients.createDefault();
                 CloseableHttpResponse response = client.execute(httpPost)) {
                if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                    result = EntityUtils.toString(response.getEntity());
                } else {
                    throw new BusinessException(RspCodeEnum.ERR99992, "POST", url);
                }
            }
            return result;
        } catch (IOException e) {
            logger.error("url[" + url + "]发送POST请求异常！", e);
            throw new BusinessException(RspCodeEnum.ERR99991, "POST", url);
        }
    }
}
