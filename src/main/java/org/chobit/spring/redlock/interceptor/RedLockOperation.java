package org.chobit.spring.redlock.interceptor;

import java.util.concurrent.TimeUnit;

/**
 * Base class for redLock operations
 *
 * @author rui.zhang
 */
public class RedLockOperation {


    /**
     * RedLock Key
     */
    private String key;


    /**
     * 等待时间
     */
    private Long waitTime;


    /**
     * 持有锁的时长
     */
    private Long leaseTime;


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

    public Long getWaitTime() {
        return waitTime;
    }

    public void setWaitTime(Long waitTime) {
        this.waitTime = waitTime;
    }

    public Long getLeaseTime() {
        return leaseTime;
    }

    public void setLeaseTime(Long leaseTime) {
        this.leaseTime = leaseTime;
    }

    public TimeUnit getTimeUnit() {
        return timeUnit;
    }

    public void setTimeUnit(TimeUnit timeUnit) {
        this.timeUnit = timeUnit;
    }
}
