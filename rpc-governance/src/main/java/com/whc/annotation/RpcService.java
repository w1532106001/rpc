package com.whc.annotation;


import java.lang.annotation.*;


/**
 * RPC服务注解，在服务实现类上标记
 *
 * @author whc
 * @date 2020/10/14
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
@Inherited
public @interface RpcService {

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
