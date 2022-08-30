package org.chobit.spring.redlock.interceptor;

import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.lang.Nullable;

import java.lang.reflect.Method;

import static jodd.util.StringUtil.isNotBlank;

/**
 * @author rui.zhang
 */
abstract class RedLockAspectSupport implements BeanFactoryAware, InitializingBean {


    private RedLockAttributeSource attrSource;

    @Nullable
    private BeanFactory beanFactory;

    public RedLockAspectSupport() {
    }

    @Override
    public void setBeanFactory(BeanFactory beanFactory) {
        this.beanFactory = beanFactory;
    }

    @Nullable
    protected final BeanFactory getBeanFactory() {
        return this.beanFactory;
    }


    public RedLockAttributeSource getAttrSource() {
        return attrSource;
    }

    public void setAttrSource(RedLockAttributeSource attrSource) {
        this.attrSource = attrSource;
    }


    @Override
    public void afterPropertiesSet() {
        if (null == this.beanFactory) {
            throw new IllegalStateException("Make sure to run within a BeanFactory containing a RedLockInterceptor bean!");
        }
        if (null == getAttrSource()) {
            throw new IllegalStateException(
                    "'redLockAttributeSource' is required: If there are no 'redLockAttributeSource', then don't use a redLock aspect.");
        }
    }


    protected Object redLockInvoke(Method method, Class<?> targetClass, final InvocationCallback invocation) throws Throwable {

        Object r;
        long start = System.currentTimeMillis();
        try {
            r = invocation.proceedWithInvocation();
        } catch (Throwable t) {
            // do something
            throw t;
        } finally {
            // do something
        }
        return r;
    }


    protected interface InvocationCallback {

        /**
         * 处理方法调用
         *
         * @return 方法执行结果
         * @throws Throwable 异常信息
         */
        Object proceedWithInvocation() throws Throwable;
    }


}