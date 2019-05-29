package cn.tang.cacheframework.cache;

import java.util.Date;
import java.util.Set;

/**
 * @author tangwenlong
 * @description: 缓存服务端
 * @date 2018/7/9 18:49
 */
public interface ICacheClient {

    boolean add(String paramString1, String paramString2, Object paramObject, int paramInt);

    boolean clear(String paramString);

    boolean put(String paramString1, String paramString2, Object paramObject);

    boolean put(String paramString1, String paramString2, Object paramObject, Date paramDate);

    boolean put(String paramString1, String paramString2, Object paramObject, int paramInt);

    Object get(String paramString1, String paramString2);

    boolean remove(String paramString1, String paramString2);

    int size(String paramString);

    Set<String> keySet(String paramString);

    boolean isExist(String paramString1, String paramString2);

}
