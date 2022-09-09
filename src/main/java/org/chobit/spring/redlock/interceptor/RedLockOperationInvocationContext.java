package org.chobit.spring.redlock.interceptor;

import java.lang.reflect.Method;

/**
 * Representation of the context of the invocation of a redLock operation.
 *
 * <p>The redLock operation is static and independent of a particular invocation;
 * this interface gathers the operation and a particular invocation.
 *
 * @author rui.zhang
 */
public interface RedLockOperationInvocationContext {

    /**
     * Return the redLock operation.
     *
     * @return The redLock operation
     */
    RedLockOperation getOperation();

    /**
     * Return the target instance on which the method was invoked.
     *
     * @return The target instance
     */
    Object getTarget();

    /**
     * Return the method which was invoked.
     *
     * @return The method
     */
    Method getMethod();

    /**
     * Return the argument list used to invoke the method.
     *
     * @return The argument list
     */
    Object[] getArgs();

}
