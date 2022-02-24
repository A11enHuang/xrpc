package com.fuller.component.xrpc;

import com.fuller.component.xrpc.consumer.ConsumerCaller;
import io.grpc.MethodDescriptor;
import io.grpc.ServerCallHandler;

import java.lang.reflect.Method;

/**
 * RPC方法相关注册器
 *
 * @author Allen Huang on 2022/2/23
 */
@SuppressWarnings("rawtypes")
public interface MethodRegister {

    /**
     * 获取某方法的gRPC方法描述。此方法是线程安全的
     *
     * @param definition 方法所属服务的服务定义
     * @param method     需要解析的方法
     * @return 返回方法描述实例
     */
    MethodDescriptor parseMethodDescriptor(ServiceDefinition definition, Method method);

    /**
     * 获取某个方法的gRPC服务点调用实例，此方法是线程安全的。
     *
     * @param bean   目标方法所属的对象实例
     * @param method 目标方法
     * @return 返回ServerCallHandler实例
     */
    ServerCallHandler parseServerCallHandler(Object bean, Method method);

    /**
     * 获取某个方法的客户端存根
     *
     * @param definition 方法所属服务的服务定义
     * @param method     RPC方法
     * @return 返回客户端存根
     */
    ConsumerCaller parseConsumerCaller(ServiceDefinition definition, Method method);

}
