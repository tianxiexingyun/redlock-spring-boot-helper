package org.chobit.spring.redlock.interceptor;

import org.springframework.aop.ClassFilter;
import org.springframework.aop.Pointcut;
import org.springframework.aop.support.AbstractBeanFactoryPointcutAdvisor;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;


/**
 * @author rui.zhang
 */
public class BeanFactoryRedLockOperationSourceAdvisor extends AbstractBeanFactoryPointcutAdvisor {

    @Nullable
    private RedLockOperationSource redLockOperationSource;


    private final RedLockOperationSourcePointcut pointcut = new RedLockOperationSourcePointcut() {
        @Override
        protected RedLockOperationSource getRedLockOperationSource() {
            return redLockOperationSource;
        }
    };


    public void setRedLockOperationSource(@Nullable RedLockOperationSource redLockOperationSource) {
        this.redLockOperationSource = redLockOperationSource;
    }


    public void setClassFilter(ClassFilter classFilter) {
        this.pointcut.setClassFilter(classFilter);
    }


    @NonNull
    @Override
    public Pointcut getPointcut() {
        return this.pointcut;
    }

}
