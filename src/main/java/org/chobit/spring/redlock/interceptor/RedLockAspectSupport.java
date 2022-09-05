package org.chobit.spring.redlock.interceptor;

import org.chobit.spring.redlock.interceptor.spel.RedLockOperationExpressionEvaluator;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.expression.AnnotatedElementKey;
import org.springframework.expression.EvaluationContext;
import org.springframework.lang.Nullable;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import java.lang.reflect.Method;

/**
 * Base class for redLock aspects, such as the {@link RedLockInterceptor} or an AspectJ aspect.
 * This enables the underlying Spring caching infrastructure to be used easily to implement an aspect for any aspect system.
 * <p>
 * Subclasses are responsible for calling relevant methods in the correct order.
 * <p>
 * Uses the Strategy design pattern. A {@link RedLockOperationSource} is used for determining caching operations,
 * a {@link KeyGenerator} will build the redLock keys.
 *
 * @author rui.zhang
 */
abstract class RedLockAspectSupport implements BeanFactoryAware, InitializingBean {


    private final RedLockOperationExpressionEvaluator evaluator = new RedLockOperationExpressionEvaluator();

    private RedLockOperationSource attrSource;

    @Nullable
    private BeanFactory beanFactory;

    public RedLockAspectSupport() {
    }

    @Override
    public void setBeanFactory(BeanFactory beanFactory) {
        this.beanFactory = beanFactory;
    }

    @Nullable
    protected final BeanFactory getBeanFactory() {
        return this.beanFactory;
    }


    public RedLockOperationSource getAttrSource() {
        return attrSource;
    }

    public void setAttrSource(RedLockOperationSource attrSource) {
        this.attrSource = attrSource;
    }


    @Override
    public void afterPropertiesSet() {
        if (null == this.beanFactory) {
            throw new IllegalStateException("Make sure to run within a BeanFactory containing a RedLockInterceptor bean!");
        }
        if (null == getAttrSource()) {
            throw new IllegalStateException(
                    "'redLockAttributeSource' is required: If there are no 'redLockAttributeSource', then don't use a redLock aspect.");
        }
    }


    protected Object redLockInvoke(Method method, Class<?> targetClass, final InvocationCallback invocation) throws Throwable {

        Object r;
        long start = System.currentTimeMillis();
        try {
            r = invocation.proceedWithInvocation();
        } catch (Throwable t) {
            // do something
            throw t;
        } finally {
            // do something
        }
        return r;
    }


    protected interface InvocationCallback {

        /**
         * 处理方法调用
         *
         * @return 方法执行结果
         * @throws Throwable 异常信息
         */
        Object proceedWithInvocation() throws Throwable;
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

        private final KeyGenerator keyGenerator;

        public RedLockOperationMetadata(RedLockOperation operation,
                                        Method method,
                                        Class<?> targetClass,
                                        Method targetMethod,
                                        AnnotatedElementKey methodKey,
                                        KeyGenerator keyGenerator) {
            this.operation = operation;
            this.method = method;
            this.targetClass = targetClass;
            this.targetMethod = targetMethod;
            this.methodKey = methodKey;
            this.keyGenerator = keyGenerator;
        }
    }


    /**
     * A {@link RedLockOperationInvocationContext} context for a {@link RedLockOperation}.
     */
    protected class RedLockOperationContext implements RedLockOperationInvocationContext {

        private final RedLockOperationMetadata metadata;

        private final Object[] args;

        private final Object target;

        @Nullable
        private Boolean conditionPassing;

        public RedLockOperationContext(RedLockOperationMetadata metadata, Object[] args, Object target) {
            this.metadata = metadata;
            this.args = extractArgs(metadata.method, args);
            this.target = target;
        }

        @Override
        public RedLockOperation getOperation() {
            return this.metadata.operation;
        }

        @Override
        public Object getTarget() {
            return this.target;
        }

        @Override
        public Method getMethod() {
            return this.metadata.method;
        }

        @Override
        public Object[] getArgs() {
            return this.args;
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
        protected Object generateKey(@Nullable Object result) {
            if (StringUtils.hasText(this.metadata.operation.getKey())) {
                EvaluationContext evaluationContext = createEvaluationContext(result);
                return evaluator.key(this.metadata.operation.getKey(), this.metadata.methodKey, evaluationContext);
            }
            return this.metadata.keyGenerator.generate(this.target, this.metadata.method, this.args);
        }

        private EvaluationContext createEvaluationContext(@Nullable Object result) {
            return evaluator.createEvaluationContext(this.metadata.method, this.args,
                    this.target, this.metadata.targetClass, this.metadata.targetMethod, result, beanFactory);
        }
    }

}