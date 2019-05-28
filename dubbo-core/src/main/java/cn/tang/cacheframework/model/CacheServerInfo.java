package cn.tang.cacheframework.model;

/**
 * Created by kevin on 2016/8/29.
 */

import java.util.Map;

public class CacheServerInfo {
    private long total;
    private long allMemory;
    private long useMemory;
    private int connectionNo;
    private long hits;
    private long misses;
    private Map<String, String> allInfo;

    public long getTotal() {
        return this.total;
    }

    public void setTotal(long total) {
        this.total = total;
    }

    public long getAllMemory() {
        return this.allMemory;
    }

    public void setAllMemory(long allMemory) {
        this.allMemory = allMemory;
    }

    public long getUseMemory() {
        return this.useMemory;
    }

    public void setUseMemory(long useMemory) {
        this.useMemory = useMemory;
    }

    public int getConnectionNo() {
        return this.connectionNo;
    }

    public void setConnectionNo(int connectionNo) {
        this.connectionNo = connectionNo;
    }

    public long getHits() {
        return this.hits;
    }

    public void setHits(long hits) {
        this.hits = hits;
    }

    public long getMisses() {
        return this.misses;
    }

    public void setMisses(long misses) {
        this.misses = misses;
    }

    public Map<String, String> getAllInfo() {
        return this.allInfo;
    }

    public void setAllInfo(Map<String, String> allInfo) {
        this.allInfo = allInfo;
    }
}
