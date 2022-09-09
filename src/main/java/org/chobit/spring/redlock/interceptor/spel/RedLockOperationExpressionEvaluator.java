package org.chobit.spring.redlock.interceptor.spel;

import org.springframework.beans.factory.BeanFactory;
import org.springframework.context.expression.AnnotatedElementKey;
import org.springframework.context.expression.BeanFactoryResolver;
import org.springframework.context.expression.CachedExpressionEvaluator;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.Expression;
import org.springframework.lang.Nullable;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Utility class handling the SpEL expression parsing.
 * Meant to be used as a reusable, thread-safe component.
 *
 * <p>Performs internal caching for performance reasons using {@link AnnotatedElementKey}.
 *
 * @author rui.zhang
 */
public class RedLockOperationExpressionEvaluator extends CachedExpressionEvaluator {


    /**
     * Indicate that there is no result variable.
     */
    public static final Object NO_RESULT = new Object();

    /**
     * Indicate that the result variable cannot be used at all.
     */
    public static final Object RESULT_UNAVAILABLE = new Object();

    /**
     * The name of the variable holding the result object.
     */
    public static final String RESULT_VARIABLE = "result";


    private final Map<ExpressionKey, Expression> keyCache = new ConcurrentHashMap<>(64);


    /**
     * Create an {@link EvaluationContext}.
     *
     * @param method      the method
     * @param args        the method arguments
     * @param target      the target object
     * @param targetClass the target class
     * @param result      the return value (can be {@code null}) or
     *                    {@link #NO_RESULT} if there is no return at this time
     * @param beanFactory the bean factory
     * @return the evaluation context
     */
    public EvaluationContext createEvaluationContext(Method method,
                                                     Object[] args,
                                                     Object target,
                                                     Class<?> targetClass,
                                                     Method targetMethod,
                                                     @Nullable Object result,
                                                     @Nullable BeanFactory beanFactory) {

        RedLockExpressionRootObject rootObject =
                new RedLockExpressionRootObject(method, args, target, targetClass);
        RedLockEvaluationContext evaluationContext =
                new RedLockEvaluationContext(rootObject, targetMethod, args, getParameterNameDiscoverer());
        if (result == RESULT_UNAVAILABLE) {
            evaluationContext.addUnavailableVariable(RESULT_VARIABLE);
        } else if (result != NO_RESULT) {
            evaluationContext.setVariable(RESULT_VARIABLE, result);
        }
        if (beanFactory != null) {
            evaluationContext.setBeanResolver(new BeanFactoryResolver(beanFactory));
        }
        return evaluationContext;
    }


    @Nullable
    public Object key(String keyExpression, AnnotatedElementKey methodKey, EvaluationContext evalContext) {
        return getExpression(this.keyCache, methodKey, keyExpression).getValue(evalContext);
    }


    /**
     * Clear all caches.
     */
    void clear() {
        this.keyCache.clear();
    }
}
