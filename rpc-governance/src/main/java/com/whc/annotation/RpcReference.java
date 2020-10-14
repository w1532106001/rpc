package com.whc.annotation;


import java.lang.annotation.*;

/**
 * RPC注解，自动装配服务实现类
 *
 * @author whc
 * @date 2020/10/14
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
@Inherited
public @interface RpcReference {

    /**
     * 服务版本，默认值为空字符串
     *
     * @return
     */
    String version() default "";

    /**
     * 服务组，默认值为空字符串
     *
     * @return
     */
    String group() default "";

}
