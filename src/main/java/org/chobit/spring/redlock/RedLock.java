package org.chobit.spring.redlock;

import java.lang.annotation.*;
import java.util.concurrent.TimeUnit;

/**
 * RedLock注解定义
 *
 * @author rui.zhang
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
public @interface RedLock {


    /**
     * RedLock Key
     *
     * @return RedLock key
     */
    String key();


    /**
     * 等待时间
     *
     * @return 等待时间
     */
    long waitTime() default 0;


    /**
     * 持有锁的时长
     *
     * @return 持有锁的时长
     */
    long leaseTime() default 1;


    /**
     * 时间单位
     *
     * @return 时间单位
     */
    TimeUnit timeUnit() default TimeUnit.SECONDS;


}
