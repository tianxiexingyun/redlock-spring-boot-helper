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
    private RedLockAttributeSource redLockAttributeSource;


    private final RedLockAttributeSourcePointcut pointcut = new RedLockAttributeSourcePointcut() {
        @Override
        protected RedLockAttributeSource getRedLockAttributeSource() {
            return redLockAttributeSource;
        }
    };


    public void setRedLockAttributeSource(RedLockAttributeSource redLockAttributeSource) {
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
