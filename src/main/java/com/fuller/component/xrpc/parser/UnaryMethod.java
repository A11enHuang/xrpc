package com.fuller.component.xrpc.parser;

import com.fuller.component.xrpc.MarshallerRegister;
import com.fuller.component.xrpc.ServiceDefinition;
import io.grpc.MethodDescriptor;
import io.grpc.ServerCallHandler;
import io.grpc.stub.ServerCalls;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 *
 * 根据服务方法服务端注册不同的服务类, 该类是一条请求消息后面跟着一条响应消息服务方法类型
 *
 * @author Leo Li on 2022/2/15
 */
@Component
@RequiredArgsConstructor
public class UnaryMethod implements MethodParser {


    private final MarshallerRegister marshallerRegister;

    @Override
    public MethodDescriptor parseDescriptor(ServiceDefinition definition, Method method) {
        return method.getParameterTypes().length < 2 ? null : MethodDescriptor.newBuilder()
                .setType(MethodDescriptor.MethodType.UNARY)
                .setFullMethodName(definition.getServiceName() + "/" + method.getName())
                .setSampledToLocalTracing(true)
                .setRequestMarshaller(marshallerRegister.getMarshaller(method.getParameterTypes().length == 0 ? String.class : method.getParameterTypes()[0]))
                .setResponseMarshaller(marshallerRegister.getMarshaller(method.getGenericReturnType()))
                .build();
    }

    @Override
    public ServerCallHandler parseServerCallHandler(Method method, Object target) {
        return ServerCalls.asyncUnaryCall((request, responseObserver) -> {
            try {
                Object invoke = method.invoke(target, request);
                responseObserver.onNext(invoke);
            } catch (IllegalAccessException | InvocationTargetException e) {
                responseObserver.onError(e);
            } finally {
                responseObserver.onCompleted();
            }
        });
    }

    @Override
    public int getOrder() {
        return 4;
    }
}
