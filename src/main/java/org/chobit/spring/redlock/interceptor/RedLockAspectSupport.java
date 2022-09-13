package org.chobit.spring.redlock.interceptor;

import org.chobit.spring.redlock.exception.RedLockException;
import org.chobit.spring.redlock.interceptor.spel.RedLockOperationExpressionEvaluator;
import org.springframework.aop.framework.AopProxyUtils;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.cglib.proxy.Proxy;
import org.springframework.context.expression.AnnotatedElementKey;
import org.springframework.expression.EvaluationContext;
import org.springframework.lang.Nullable;
import org.springframework.util.ObjectUtils;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static jodd.util.StringUtil.isBlank;

/**
 * Base class for redLock aspects, such as the {@link RedLockInterceptor} or an AspectJ aspect.
 * This enables the underlying Spring caching infrastructure to be used easily to implement an aspect for any aspect system.
 * <p>
 * Subclasses are responsible for calling relevant methods in the correct order.
 * <p>
 * Uses the Strategy design pattern. A {@link RedLockOperationSource} is used for determining caching operations.
 *
 * @author rui.zhang
 */
abstract class RedLockAspectSupport implements BeanFactoryAware, InitializingBean {


    private final Map<RedLockOperationKey, RedLockOperationMetadata> metadataCache = new ConcurrentHashMap<>(1024);

    private final RedLockOperationExpressionEvaluator evaluator = new RedLockOperationExpressionEvaluator();

    private RedLockOperationSource redLockOperationSource;

    @Nullable
    private BeanFactory beanFactory;

    public RedLockAspectSupport() {
    }

    @Override
    public void setBeanFactory(@Nullable BeanFactory beanFactory) {
        this.beanFactory = beanFactory;
    }

    @Nullable
    protected final BeanFactory getBeanFactory() {
        return this.beanFactory;
    }


    public RedLockOperationSource getRedLockOperationSource() {
        return redLockOperationSource;
    }

    public void setRedLockOperationSource(RedLockOperationSource redLockOperationSource) {
        this.redLockOperationSource = redLockOperationSource;
    }


    @Override
    public void afterPropertiesSet() {
        if (null == this.beanFactory) {
            throw new IllegalStateException("Make sure to run within a BeanFactory containing a RedLockInterceptor bean!");
        }
        if (null == getRedLockOperationSource()) {
            throw new IllegalStateException(
                    "'redLockAttributeSource' is required: If there are no 'redLockAttributeSource', then don't use a redLock aspect.");
        }
    }


    protected Object execute(final RedLockOperationInvoker invoker, Object target, Method method, Object[] args) throws Throwable {
        Class<?> targetClass = getTargetClass(target);
        RedLockOperationSource operationSource = getRedLockOperationSource();
        if (null != operationSource) {
            RedLockOperation operation = operationSource.getRedLockOperation(method, targetClass);
            RedLockOperationContext context = createOperationContext(operation, method, args, target, targetClass);
            return execute(invoker, context);
        }
        return invoker.invoke();
    }


    private Class<?> getTargetClass(Object target) {
        return AopProxyUtils.ultimateTargetClass(target);
    }

    private Object execute(final RedLockOperationInvoker invoker, RedLockOperationContext context) {
        Object key = generateKey(context);
        long start = System.currentTimeMillis();
        try {
            return invoker.invoke();
        } finally {
            System.out.println(key);
            System.out.println("Duration : " + (System.currentTimeMillis() - start));
        }
    }


    private Object generateKey(RedLockOperationContext context) {
        Object key = context.generateKey();
        if (null == key) {
            throw new IllegalArgumentException("Null key returned for redLock operation (maybe you are " +
                    "using named params on classes without debug info?) " + context.metadata.operation);
        }
        return key;
    }


    protected RedLockOperationContext createOperationContext(RedLockOperation operation,
                                                             Method method,
                                                             Object[] args,
                                                             Object target,
                                                             Class<?> targetClass) {
        RedLockOperationMetadata metadata = createRedLockOperationMetadata(operation, method, targetClass);
        return new RedLockOperationContext(metadata, args, target);
    }


    /**
     * Return the {@link RedLockAspectSupport.RedLockOperationMetadata} for the specified operation.
     * <p>Resolve the {@link org.springframework.cache.interceptor.KeyGenerator} to be
     * used for the operation.
     *
     * @param operation   the operation
     * @param method      the method on which the operation is invoked
     * @param targetClass the target type
     * @return the resolved metadata for the operation
     */
    protected RedLockOperationMetadata createRedLockOperationMetadata(RedLockOperation operation,
                                                                      Method method,
                                                                      Class<?> targetClass) {
        RedLockOperationKey operationKey = new RedLockOperationKey(operation, method, targetClass);
        RedLockOperationMetadata metadata = this.metadataCache.get(operationKey);
        if (null == metadata) {
            metadata = new RedLockOperationMetadata(operation, method, targetClass);
            this.metadataCache.put(operationKey, metadata);
        }
        return metadata;
    }


    private static final class RedLockOperationKey implements Comparable<RedLockOperationKey> {

        private final RedLockOperation operation;

        private final AnnotatedElementKey methodKey;

        private RedLockOperationKey(RedLockOperation operation, Method method, Class<?> targetClass) {
            this.operation = operation;
            this.methodKey = new AnnotatedElementKey(method, targetClass);
        }

        @Override
        public boolean equals(Object other) {
            if (this == other) {
                return true;
            }
            if (!(other instanceof RedLockOperationKey)) {
                return false;
            }
            RedLockOperationKey otherKey = (RedLockOperationKey) other;
            return (this.operation.equals(otherKey.operation) && this.methodKey.equals(otherKey.methodKey));
        }

        @Override
        public int hashCode() {
            return (this.operation.hashCode() * 31 + this.methodKey.hashCode());
        }

        @Override
        public String toString() {
            return this.operation + " on " + this.methodKey;
        }

        @Override
        public int compareTo(RedLockOperationKey other) {
            int result = this.operation.getKey().compareTo(other.operation.getKey());
            if (result == 0) {
                result = this.methodKey.compareTo(other.methodKey);
            }
            return result;
        }
    }


    /**
     * Metadata of a redLock operation that does not depend on a particular invocation
     * which makes it a good candidate for redLock.
     */
    protected static class RedLockOperationMetadata {

        private final RedLockOperation operation;

        private final Method method;

        private final Class<?> targetClass;

        private final Method targetMethod;

        private final AnnotatedElementKey methodKey;

        public RedLockOperationMetadata(RedLockOperation operation,
                                        Method method,
                                        Class<?> targetClass) {
            this.operation = operation;
            this.method = method;
            this.targetClass = targetClass;
            this.targetMethod = (!Proxy.isProxyClass(targetClass) ? AopUtils.getMostSpecificMethod(method, targetClass) : this.method);
            this.methodKey = new AnnotatedElementKey(this.targetMethod, targetClass);
        }
    }


    /**
     * A context for a {@link RedLockOperation}.
     */
    protected class RedLockOperationContext {

        private final RedLockOperationMetadata metadata;

        private final Object[] args;

        private final Object target;

        public RedLockOperationContext(RedLockOperationMetadata metadata, Object[] args, Object target) {
            this.metadata = metadata;
            this.args = extractArgs(metadata.method, args);
            this.target = target;
        }

        private Object[] extractArgs(Method method, Object[] args) {
            if (!method.isVarArgs()) {
                return args;
            }
            Object[] varArgs = ObjectUtils.toObjectArray(args[args.length - 1]);
            Object[] combinedArgs = new Object[args.length - 1 + varArgs.length];
            System.arraycopy(args, 0, combinedArgs, 0, args.length - 1);
            System.arraycopy(varArgs, 0, combinedArgs, args.length - 1, varArgs.length);
            return combinedArgs;
        }

        /**
         * Compute the key for the given caching operation.
         */
        @Nullable
        protected Object generateKey() {
            if (isBlank(this.metadata.operation.getKey())) {
                throw new RedLockException("The key for redLock is blank.");
            }
            EvaluationContext evaluationContext = createEvaluationContext();
            return evaluator.key(this.metadata.operation.getKey(), this.metadata.methodKey, evaluationContext);
        }

        private EvaluationContext createEvaluationContext() {
            return evaluator.createEvaluationContext(this.metadata.method, this.args,
                    this.target, this.metadata.targetClass, this.metadata.targetMethod, beanFactory);
        }
    }

}