package com.fuller.component.xrpc.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 标记一个属性为XRPC对象，将会自动注入XRPC实例
 *
 * @author Allen Huang on 2022/2/16
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface XRPCReference {

    /**
     * 配置直连模式，此消费者将会直连目标url
     */
    String hostname() default "";

    /**
     * 配置直连模式的端口号
     */
    int port() default -1;

}
