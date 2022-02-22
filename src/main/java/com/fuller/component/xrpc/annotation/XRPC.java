package com.fuller.component.xrpc.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 标记一个接口为XRPC接口
 *
 * @author Allen Huang on 2022/2/10
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface XRPC {
    /**
     * 微服务的应用名称
     */
    String hostname();
    /**
     * 服务端口号
     */
    int port() default 8001;
    /**
     * 当前服务暴露的名称
     */
    String serviceName() default "";
    /**
     * 当前服务所属的包名
     */
    String servicePackage() default "";
    /**
     * 版本号
     */
    String version() default "v1";

    String qualifier() default "";

    boolean primary() default true;

}
