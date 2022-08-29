package org.chobit.spring.redlock.interceptor;

import java.lang.reflect.Method;

/**
 * RedLock属性源
 *
 * @author rui.zhang
 */
public interface RedLockAttributeSource {


    /**
     * 获取方法的RedLock配置
     *
     * @param method      相关
     * @param targetClass 目标类
     * @return RedLock相关属性
     */
    RedLockAttribute getRedLockAttribute(Method method, Class<?> targetClass);

}
