package com.fuller.component.xrpc;

import io.grpc.MethodDescriptor;
import io.grpc.ServerCallHandler;

import java.lang.reflect.Method;

/**
 * 方法描述注册器，维护了应用内的所有方法描述
 *
 * @author Allen Huang on 2022/2/10
 */
@SuppressWarnings("rawtypes")
public interface MethodRegister {

    /**
     * 根据目标方法获取该方法的gRPC描述。此方法是线程安全的。
     * 如果当前方法不存在方法描述，则会使用默认的工厂创建方法描述
     *
     * @param definition 服务定义
     * @param method     目标方法
     * @return 返回方法描述实例
     */
    MethodDescriptor getMethodDescriptor(ServiceDefinition definition, Method method);

    /**
     * 根据方法获取该方法的服务端处理器。此方法是线程安全的。
     *
     * @param definition 服务的定义信息
     * @param method     目标方法
     * @return 返回handler实例
     */
    ServerCallHandler getServerCallHandler(ServiceDefinition definition, Method method);

}
