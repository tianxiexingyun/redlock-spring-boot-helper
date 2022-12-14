package org.chobit.spring.redlock;


import org.springframework.boot.autoconfigure.AutoConfigurationPackage;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * kafka stater annotation
 *
 * @author rui.zhang
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
@AutoConfigurationPackage
@Import(RedLockConfiguration.class)
public @interface EnableRedLock {


}
