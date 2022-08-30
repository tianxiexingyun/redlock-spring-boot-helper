package org.chobit.spring.redlock.interceptor;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.aop.support.AopUtils;

import java.io.Serializable;


/**
 * RedLock Interceptor
 *
 * @author rui.zhang
 */
public class RedLockInterceptor extends RedLockAspectSupport implements MethodInterceptor, Serializable {


    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {
        Class<?> targetClass = (invocation.getThis() != null ? AopUtils.getTargetClass(invocation.getThis()) : null);
        return redLockInvoke(invocation.getMethod(), targetClass, invocation::proceed);
    }

}
