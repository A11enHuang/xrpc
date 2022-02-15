package com.fuller.component.xrpc.parser;

import com.fuller.component.xrpc.ServiceDefinition;
import io.grpc.MethodDescriptor;
import io.grpc.ServerCallHandler;
import org.springframework.core.Ordered;

import java.lang.reflect.Method;

/**
 * 方法解析器
 *
 * @author Allen Huang on 2022/2/11
 */
@SuppressWarnings("rawtypes")
public interface MethodParser extends Ordered {

    /**
     * 解析方法描述
     *
     * @param definition 服务定义
     * @param method     目标方法
     * @return 如果该解析器无法解析此方法，将返回null
     */
    MethodDescriptor parseDescriptor(ServiceDefinition definition, Method method);

    /**
     * 解析此方法并生成服务端调用handler实例
     *
     * @param method 需要解析的方法
     * @param target 调用目标对象
     * @return handler实例
     */
    ServerCallHandler parseServerCallHandler(Method method, Object target);

    @Override
    default int getOrder() {
        return 0;
    }

}
