package org.chobit.spring.redlock.interceptor;

import org.springframework.lang.Nullable;

import java.lang.reflect.Method;

/**
 * Interface used by {@link RedLockInterceptor}. Implementations know how to source redlock attributes, whether from configuration, metadata attributes at source level, or elsewhere.
 *
 * @author rui.zhang
 */
public interface RedLockOperationSource {


    /**
     * 获取方法的RedLock配置
     *
     * @param method      相关
     * @param targetClass 目标类
     * @return RedLock相关属性
     */
    @Nullable
    RedLockOperation getRedLockAttribute(Method method, @Nullable Class<?> targetClass);

}
