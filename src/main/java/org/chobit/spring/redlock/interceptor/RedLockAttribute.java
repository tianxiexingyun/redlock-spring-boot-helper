package org.chobit.spring.redlock.interceptor;

import java.util.concurrent.TimeUnit;

/**
 * RedLock配置信息
 *
 * @author rui.zhang
 */
public class RedLockAttribute {


    /**
     * RedLock Key
     */
    private String key;

    /**
     * 等待时间
     */
    private long waitTime;


    /**
     * 持有锁的时长
     */
    private long leaseTime;


    /**
     * 时间单元
     */
    private TimeUnit timeUnit;


    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public long getWaitTime() {
        return waitTime;
    }

    public void setWaitTime(long waitTime) {
        this.waitTime = waitTime;
    }

    public long getLeaseTime() {
        return leaseTime;
    }

    public void setLeaseTime(long leaseTime) {
        this.leaseTime = leaseTime;
    }

    public TimeUnit getTimeUnit() {
        return timeUnit;
    }

    public void setTimeUnit(TimeUnit timeUnit) {
        this.timeUnit = timeUnit;
    }
}
