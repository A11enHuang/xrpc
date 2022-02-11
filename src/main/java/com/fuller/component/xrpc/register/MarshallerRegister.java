package com.fuller.component.xrpc.register;

import io.grpc.MethodDescriptor;

import java.lang.reflect.Type;

/**
 * 序列化器注册器
 *
 * @author Allen Huang on 2022/2/10
 */
@SuppressWarnings("rawtypes")
public interface MarshallerRegister {

    /**
     * 注册一个序列化器。此方法是线程安全的。
     * 如果该类型已经存在相同的序列化器，则注册将会失效。
     *
     * @param type       序列化器对应的类型
     * @param marshaller 序列化器实例
     */
    void registerMarshaller(Type type, MethodDescriptor.Marshaller marshaller);

    /**
     * 根据类型获取该类型对应的序列化器实例。此方法是线程安全的。
     * 如果当前不存在序列化器实例，则会尝试使用默认的工厂创建序列化器实例。
     *
     * @param type 需要序列化的目标类
     * @return 如果不存在此序列化器实例
     */
    MethodDescriptor.Marshaller getMarshaller(Type type);

}
