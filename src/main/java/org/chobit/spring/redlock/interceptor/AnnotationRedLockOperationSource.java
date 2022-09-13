package org.chobit.spring.redlock.interceptor;

import org.chobit.spring.redlock.RedLock;
import org.springframework.core.BridgeMethodResolver;
import org.springframework.core.MethodClassKey;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.util.ClassUtils;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 * Implementation of the {@link RedLockOperationSource} interface
 * for working with caching metadata in annotation format.
 *
 * <p>This class reads Spring's {@link RedLock} annotations
 * and exposes corresponding redLock operation definition to Spring's infrastructure.
 * This class may also serve as base class for a custom {@link RedLockOperationSource}.
 *
 * @author rui.zhang
 */
public class AnnotationRedLockOperationSource implements RedLockOperationSource, Serializable {


    /**
     * 缓存
     */
    private final Map<Object, Optional<RedLockOperation>> attrCache =
            new ConcurrentHashMap<>(1024);


    @Override
    public RedLockOperation getRedLockOperation(Method method, Class<?> targetClass) {

        if (method.getDeclaringClass() == Object.class) {
            return null;
        }

        Object cacheKey = getCacheKey(method, targetClass);

        if (attrCache.containsKey(cacheKey)) {
            RedLockOperation attr = computeRedLockAttribute(method, targetClass);
            if (null == attr) {
                attrCache.put(cacheKey, Optional.empty());
                return null;
            }
            attrCache.put(cacheKey, Optional.of(attr));
            return attr;
        } else {
            Optional<RedLockOperation> opt = attrCache.get(cacheKey);
            return opt.orElse(null);
        }
    }

    private RedLockOperation computeRedLockAttribute(Method method, Class<?> targetClass) {

        RedLockOperation attr = computeRedLockAttribute(method);
        if (null != attr) {
            return attr;
        }

        Class<?> userClass = ClassUtils.getUserClass(targetClass);
        Method specificMethod = ClassUtils.getMostSpecificMethod(method, userClass);
        specificMethod = BridgeMethodResolver.findBridgedMethod(specificMethod);

        if (specificMethod != method) {
            attr = computeRedLockAttribute(method);
            return attr;
        }
        return null;
    }


    private RedLockOperation computeRedLockAttribute(Method method) {
        if (method.getAnnotations().length > 0) {
            return parseRedLockAttribute(method);
        }
        return null;
    }

    private RedLockOperation parseRedLockAttribute(Method method) {
        AnnotationAttributes attributes =
                AnnotatedElementUtils.getMergedAnnotationAttributes(method, RedLock.class);
        if (null != attributes) {
            return parseRedLockAttribute(attributes);
        } else {
            return null;
        }
    }

    private RedLockOperation parseRedLockAttribute(AnnotationAttributes attributes) {
        String key = attributes.getString("key");
        Long waitTime = attributes.getNumber("waitTime");
        Long leaseTime = attributes.getNumber("leaseTime");
        TimeUnit unit = (TimeUnit) attributes.get("timeUnit");

        RedLockOperation attr = new RedLockOperation();
        attr.setKey(key);
        attr.setWaitTime(waitTime);
        attr.setLeaseTime(leaseTime);
        attr.setTimeUnit(unit);
        return attr;
    }


    private Object getCacheKey(Method method, Class<?> targetClass) {
        return new MethodClassKey(method, targetClass);
    }


}
