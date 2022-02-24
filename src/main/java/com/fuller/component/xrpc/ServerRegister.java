package com.fuller.component.xrpc;

import io.grpc.ServiceDescriptor;

/**
 * @author Allen Huang on 2022/2/23
 */
public interface ServerRegister {

    /**
     * 将目标Class解析成ServiceDefinition对象。
     * 此方法生成的对象不是单利的，每一次获取的对象实例都是不同的
     *
     * @param type 目标类
     * @return ServiceDefinition 实例
     */
    ServiceDefinition parseServiceDefinition(Class<?> type);

    /**
     * 根据目标类解析生成服务描述信息。此方法生成的服务描述信息是单例的
     *
     * @param type 目标类
     * @return 服务描述信息实例
     */
    ServiceDescriptor parseServiceDescriptor(Class<?> type);

}
