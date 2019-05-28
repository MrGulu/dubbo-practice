package cn.tang.cacheframework.cache;

import java.util.Set;

public interface ICacheAdmin {

    void init();

    void init(String paramString);

    void stop();

    boolean clear(String paramString);

    Set<String> search(String paramString1, String paramString2);

}
