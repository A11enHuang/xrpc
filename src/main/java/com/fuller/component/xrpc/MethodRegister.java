package com.fuller.component.xrpc;

import com.fuller.component.xrpc.consumer.ConsumerCaller;
import io.grpc.MethodDescriptor;
import io.grpc.ServerCallHandler;

import java.lang.reflect.Method;

/**
 * @author Allen Huang on 2022/2/23
 */
@SuppressWarnings("rawtypes")
public interface MethodRegister {

    MethodDescriptor parseMethodDescriptor(ServiceDefinition definition, Method method);

    ServerCallHandler parseServerCallHandler(Object bean, Method method);

    ConsumerCaller parseConsumerCaller(ServiceDefinition definition, Method method);

}
