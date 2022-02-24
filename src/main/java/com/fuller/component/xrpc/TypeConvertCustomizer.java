package com.fuller.component.xrpc;

import org.springframework.core.Ordered;

/**
 * 自定义类型转换，将更多的类型转换器注册到系统中
 *
 * @author Allen Huang on 2022/2/24
 */
public interface TypeConvertCustomizer extends Ordered {

    /**
     * 自定义类型转换器，在方法中向注册器中注册类型转换器
     *
     * @param register 注册器
     */
    void customize(TypeConvertRegister register);

    @Override
    default int getOrder() {
        return 0;
    }
}
