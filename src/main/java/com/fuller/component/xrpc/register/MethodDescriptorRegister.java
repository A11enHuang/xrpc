package com.fuller.component.xrpc.register;

import com.fuller.component.xrpc.ServiceDefinition;
import io.grpc.MethodDescriptor;

import java.lang.reflect.Method;

/**
 * 方法描述注册器，维护了应用内的所有方法描述
 *
 * @author Allen Huang on 2022/2/10
 */
@SuppressWarnings("rawtypes")
public interface MethodDescriptorRegister {

    /**
     * 根据目标方法获取该方法的gRPC描述。此方法是线程安全的。
     * 如果当前方法不存在方法描述，则会使用默认的工厂创建方法描述
     *
     * @param definition 服务定义
     * @param method     目标方法
     * @return 返回方法描述实例
     */
    MethodDescriptor getMethodDescriptor(ServiceDefinition definition, Method method);

}
