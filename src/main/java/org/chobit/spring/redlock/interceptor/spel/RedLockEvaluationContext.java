package org.chobit.spring.redlock.interceptor.spel;

import org.springframework.context.expression.MethodBasedEvaluationContext;
import org.springframework.core.ParameterNameDiscoverer;

import java.lang.reflect.Method;

/**
 * RedLock specific evaluation context that adds a method parameters as SpEL variables, in a lazy manner.
 * The lazy nature eliminates unneeded parsing of classes byte code for parameter discovery.
 *
 * <p>Also define a set of "unavailable variables" (i.e. variables that should lead to an exception right the way when they are accessed).
 * This can be useful to verify a condition does not match even when not all potential variables are present.
 *
 * <p>To limit the creation of objects, an ugly constructor is used (rather than a dedicated 'closure'-like class for deferred execution).
 *
 * @author rui.zhang
 */
public class RedLockEvaluationContext extends MethodBasedEvaluationContext {


    public RedLockEvaluationContext(Object rootObject,
                                    Method method,
                                    Object[] arguments,
                                    ParameterNameDiscoverer parameterNameDiscoverer) {
        super(rootObject, method, arguments, parameterNameDiscoverer);
    }


}
