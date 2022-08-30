package org.chobit.spring.redlock.interceptor;

import org.chobit.spring.redlock.RedLock;
import org.springframework.core.BridgeMethodResolver;
import org.springframework.core.MethodClassKey;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.util.ClassUtils;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 * RedLock 注解属性源
 *
 * @author rui.zhang
 */
public class AnnotationRedLockAttributeSource implements RedLockAttributeSource {


    /**
     * 缓存
     */
    private final Map<Object, Optional<RedLockAttribute>> attrCache =
            new ConcurrentHashMap<>(1024);


    @Override
    public RedLockAttribute getRedLockAttribute(Method method, Class<?> targetClass) {

        if (method.getDeclaringClass() == Object.class) {
            return null;
        }

        Object cacheKey = getCacheKey(method, targetClass);
        Optional<RedLockAttribute> cached = attrCache.get(cacheKey);

        if (null == cached) {
            RedLockAttribute attr = computeRedLockAttribute(method, targetClass);
            if (null == attr) {
                attrCache.put(cacheKey, Optional.empty());
                return null;
            }

            attrCache.put(cacheKey, Optional.of(attr));
            return attr;
        } else {
            return cached.orElse(null);
        }
    }

    private RedLockAttribute computeRedLockAttribute(Method method, Class<?> targetClass) {

        RedLockAttribute attr = computeRedLockAttribute(method);
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



    private RedLockAttribute computeRedLockAttribute(Method method) {
        if (method.getAnnotations().length > 0) {
            return parseRedLockAttribute(method);
        }
        return null;
    }

    private RedLockAttribute parseRedLockAttribute(Method method) {
        AnnotationAttributes attributes =
                AnnotatedElementUtils.getMergedAnnotationAttributes(method, RedLock.class);
        if (null != attributes) {
            return parseRedLockAttribute(attributes);
        } else {
            return null;
        }
    }

    private RedLockAttribute parseRedLockAttribute(AnnotationAttributes attributes) {
        String key = attributes.getString("key");
        Long waitTime = attributes.getNumber("waitTime");
        Long leaseTime = attributes.getNumber("leaseTime");
        TimeUnit unit = (TimeUnit) attributes.get("timeUnit");

        RedLockAttribute attr = new RedLockAttribute();
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
