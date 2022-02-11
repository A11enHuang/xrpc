package com.fuller.component.xrpc;

import io.grpc.MethodDescriptor;

import java.lang.reflect.Type;

/**
 * Marshaller工厂
 *
 * @author Allen Huang on 2022/2/11
 */
public interface MarshallerFactory {

    /**
     * 根据类型创建出序列化器。
     *
     * @param type 序列化器的目标类型
     * @return 返回实例
     */
    MethodDescriptor.Marshaller getMarshaller(Type type);

}
