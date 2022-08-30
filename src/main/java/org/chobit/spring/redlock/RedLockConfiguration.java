package org.chobit.spring.redlock;

import org.chobit.spring.redlock.interceptor.AnnotationRedLockAttributeSource;
import org.chobit.spring.redlock.interceptor.BeanFactoryRedLockAttributeSourceAdvisor;
import org.chobit.spring.redlock.interceptor.RedLockAttributeSource;
import org.chobit.spring.redlock.interceptor.RedLockInterceptor;
import org.redisson.Redisson;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Role;

/**
 * @author rui.zhang
 */
@Role(BeanDefinition.ROLE_INFRASTRUCTURE)
@Configuration
@ConditionalOnClass({Redisson.class})
@EnableConfigurationProperties(RedisProperties.class)
public class RedLockConfiguration {


    private final RedisProperties properties;


    public RedLockConfiguration(RedisProperties redisProperties) {
        this.properties = redisProperties;
    }

    @Bean
    @Role(BeanDefinition.ROLE_INFRASTRUCTURE)
    public RedLockAttributeSource watcherAttributeSource() {
        return new AnnotationRedLockAttributeSource();
    }


    @Bean
    @Role(BeanDefinition.ROLE_INFRASTRUCTURE)
    public RedLockInterceptor redLockInterceptor(RedisProperties properties) {
        RedLockInterceptor interceptor = new RedLockInterceptor();
        interceptor.setAttrSource(watcherAttributeSource());
        return interceptor;
    }


    @Bean
    @Role(BeanDefinition.ROLE_INFRASTRUCTURE)
    public BeanFactoryRedLockAttributeSourceAdvisor redLockAdvisor(RedisProperties properties) {
        BeanFactoryRedLockAttributeSourceAdvisor advisor = new BeanFactoryRedLockAttributeSourceAdvisor();
        advisor.setRedLockAttributeSource(watcherAttributeSource());
        advisor.setAdvice(redLockInterceptor(properties));
        return advisor;
    }


}
