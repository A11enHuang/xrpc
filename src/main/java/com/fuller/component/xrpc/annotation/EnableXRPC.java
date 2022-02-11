package com.fuller.component.xrpc.annotation;

import com.fuller.component.xrpc.configure.XRPCRegistrar;
import org.springframework.context.annotation.Import;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author Allen Huang on 2022/2/10
 */
@Target(ElementType.TYPE)
@Import(XRPCRegistrar.class)
@Retention(RetentionPolicy.RUNTIME)
public @interface EnableXRPC {

    /**
     * 需要扫描的包
     */
    String[] basePackages() default {};

    /**
     * 将会扫描class所在的包
     */
    Class<?>[] basePackageClasses() default {};

    /**
     * 需要导入的类
     */
    Class<?>[] clients() default {};

}
