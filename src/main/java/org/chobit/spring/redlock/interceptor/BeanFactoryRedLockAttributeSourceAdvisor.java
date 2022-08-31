package org.chobit.spring.redlock.interceptor;

import org.springframework.aop.ClassFilter;
import org.springframework.aop.Pointcut;
import org.springframework.aop.support.AbstractBeanFactoryPointcutAdvisor;
import org.springframework.lang.Nullable;


/**
 * @author rui.zhang
 */
public class BeanFactoryRedLockAttributeSourceAdvisor extends AbstractBeanFactoryPointcutAdvisor {

    @Nullable
    private RedLockOperationSource redLockAttributeSource;


    private final RedLockOperationSourcePointcut pointcut = new RedLockOperationSourcePointcut() {
        @Override
        protected RedLockOperationSource getRedLockAttributeSource() {
            return redLockAttributeSource;
        }
    };


    public void setRedLockAttributeSource(RedLockOperationSource redLockAttributeSource) {
        this.redLockAttributeSource = redLockAttributeSource;
    }


    public void setClassFilter(ClassFilter classFilter) {
        this.pointcut.setClassFilter(classFilter);
    }


    @Override
    public Pointcut getPointcut() {
        return this.pointcut;
    }

}
