package org.chobit.spring.redlock.interceptor;

import org.springframework.aop.support.StaticMethodMatcherPointcut;
import org.springframework.lang.Nullable;
import org.springframework.util.ObjectUtils;

import java.io.Serializable;
import java.lang.reflect.Method;

/**
 * A Pointcut that matches if the underlying {@link RedLockOperationSource} has an attribute for a given method.
 *
 * @author rui.zhang
 */
public abstract class RedLockOperationSourcePointcut extends StaticMethodMatcherPointcut implements Serializable {


    @Override
    public boolean matches(Method method, Class<?> targetClass) {
        RedLockOperationSource was = getRedLockOperationSource();
        return (null != was && null != was.getRedLockAttribute(method, targetClass));
    }


    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof RedLockOperationSourcePointcut)) {
            return false;
        }
        RedLockOperationSourcePointcut otherPc = (RedLockOperationSourcePointcut) other;
        return ObjectUtils.nullSafeEquals(this.getRedLockOperationSource(), otherPc.getRedLockOperationSource());
    }


    @Override
    public int hashCode() {
        return RedLockOperationSourcePointcut.class.hashCode();
    }

    @Override
    public String toString() {
        return getClass().getName() + ": " + getRedLockOperationSource();
    }


    /**
     * 获取RedLock Attribute 源
     *
     * @return RedLock Attribute 源
     */
    @Nullable
    protected abstract RedLockOperationSource getRedLockOperationSource();

}
