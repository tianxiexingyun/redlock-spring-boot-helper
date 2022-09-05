package org.chobit.spring.redlock.interceptor;

import java.lang.reflect.Method;

/**
 * RedLock key generator. Used for creating a key based on the given method
 * (used as context) and its parameters.
 *
 * @author robin
 */
@FunctionalInterface
public interface KeyGenerator {

    /**
     * Generate a key for the given method and its parameters.
     *
     * @param target the target instance
     * @param method the method being called
     * @param params the method parameters (with any var-args expanded)
     * @return a generated key
     */
    Object generate(Object target, Method method, Object... params);

}