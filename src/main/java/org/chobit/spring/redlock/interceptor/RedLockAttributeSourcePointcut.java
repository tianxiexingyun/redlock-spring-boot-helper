package org.chobit.spring.redlock.interceptor;

import org.springframework.aop.support.StaticMethodMatcherPointcut;
import org.springframework.lang.Nullable;
import org.springframework.util.ObjectUtils;

import java.io.Serializable;
import java.lang.reflect.Method;

/**
 * RedLock Attribute Source Pointcut
 */
public abstract class RedLockAttributeSourcePointcut extends StaticMethodMatcherPointcut implements Serializable {


    @Override
    public boolean matches(Method method, Class<?> targetClass) {
        RedLockAttributeSource was = getRedLockAttributeSource();
        return (null != was && null != was.getRedLockAttribute(method, targetClass));
    }


    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof RedLockAttributeSourcePointcut)) {
            return false;
        }
        RedLockAttributeSourcePointcut otherPc = (RedLockAttributeSourcePointcut) other;
        return ObjectUtils.nullSafeEquals(this.getRedLockAttributeSource(), otherPc.getRedLockAttributeSource());
    }


    @Override
    public int hashCode() {
        return RedLockAttributeSourcePointcut.class.hashCode();
    }

    @Override
    public String toString() {
        return getClass().getName() + ": " + getRedLockAttributeSource();
    }


    /**
     * 获取RedLock Attribute 源
     *
     * @return RedLock Attribute 源
     */
    @Nullable
    protected abstract RedLockAttributeSource getRedLockAttributeSource();

}
