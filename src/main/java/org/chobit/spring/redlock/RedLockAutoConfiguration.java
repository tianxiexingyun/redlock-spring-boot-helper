package org.chobit.spring.redlock;

import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * @author rui.zhang
 */
@Configuration
@ConditionalOnClass({RedLockAutoConfiguration.class})
@EnableConfigurationProperties(RedisProperties.class)
public class RedLockAutoConfiguration {
}
