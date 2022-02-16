package com.fuller.component.xrpc.parser;

import com.fuller.component.xrpc.MarshallerRegister;
import com.fuller.component.xrpc.ServiceDefinition;
import io.grpc.MethodDescriptor;
import io.grpc.ServerCallHandler;
import io.grpc.stub.ServerCalls;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.concurrent.Flow;

/**
 *
 * 根据服务方法服务端注册不同的服务类, 该类是零个或多个请求和随时响应消息服务方法类型。
 * @author Leo Li on 2022/2/16
 */
@Component
@RequiredArgsConstructor
public class BidiStreamingMethod implements MethodParser {

    private final MarshallerRegister marshallerRegister;

    @Override
    public MethodDescriptor parseDescriptor(ServiceDefinition definition, Method method) {
        Class<?>[] parameterTypes = method.getParameterTypes();
        boolean parameterTypeHasPublisher = false;
        for (Class clazz : parameterTypes) {
            boolean assignableFrom = clazz.isAssignableFrom(Flow.Publisher.class);
            if (assignableFrom){
                parameterTypeHasPublisher = true;
                break;
            }
        }
        boolean assignableFrom = method.getReturnType().isAssignableFrom(Flow.Subscriber.class);
        return parameterTypeHasPublisher && assignableFrom ? MethodDescriptor.newBuilder()
                .setType(MethodDescriptor.MethodType.BIDI_STREAMING)
                .setFullMethodName(definition.getServiceName() + "/" + method.getName())
                .setSampledToLocalTracing(true)
                .setRequestMarshaller(marshallerRegister.getMarshaller(method.getParameterTypes().length == 0 ? String.class : method.getParameterTypes()[0]))
                .setResponseMarshaller(marshallerRegister.getMarshaller(method.getGenericReturnType()))
                .build() : null;
    }

    @Override
    public ServerCallHandler parseServerCallHandler(Method method, Object target) {
        return ServerCalls.asyncBidiStreamingCall(responseObserver -> {
            return responseObserver;
        });
    }

    @Override
    public int getOrder() {
        return 1;
    }
}
