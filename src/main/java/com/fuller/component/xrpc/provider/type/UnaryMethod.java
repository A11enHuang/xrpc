package com.fuller.component.xrpc.provider.type;

import com.fuller.component.xrpc.MarshallerRegister;
import com.fuller.component.xrpc.ServiceDefinition;
import com.fuller.component.xrpc.parser.MethodParser;
import com.google.common.base.Preconditions;
import io.grpc.MethodDescriptor;
import io.grpc.ServerCallHandler;
import io.grpc.stub.ServerCalls;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 *
 * 根据服务方法服务端注册不同的服务类, 该类是客户端一次请求后跟着服务端一次返回服务方法类型
 *
 * @author Leo Li on 2022/2/15
 */
@Component
@RequiredArgsConstructor
public class UnaryMethod implements MethodParser {


    private final MarshallerRegister marshallerRegister;

    @Override
    public MethodDescriptor parseDescriptor(ServiceDefinition definition, Method method) {
        Preconditions.checkArgument(method.getParameterTypes().length < 2);
        return MethodDescriptor.newBuilder()
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

}
