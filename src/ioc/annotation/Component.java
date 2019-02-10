/**
 * Alipay.com Inc. Copyright (c) 2004-2019 All Rights Reserved.
 */
package ioc.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 *
 * @author lanzhang
 * @version $Id: Component.java, v 0.1 2019年02月09日 上午11:25 lanzhang Exp $
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Component {
   String value() default "";
}