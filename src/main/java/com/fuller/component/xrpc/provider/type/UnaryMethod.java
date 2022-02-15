package com.fuller.component.xrpc.provider.type;

import com.fuller.component.xrpc.MarshallerRegister;
import com.fuller.component.xrpc.ServiceDefinition;
import com.fuller.component.xrpc.parser.MethodParser;
import io.grpc.MethodDescriptor;
import io.grpc.ServerCallHandler;
import io.grpc.stub.ServerCalls;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;

/**
 * @author Leo Li on 2022/2/15
 */
@Component
@RequiredArgsConstructor
public class UnaryMethod implements MethodParser {


    private final MarshallerRegister marshallerRegister;

    @Override
    public MethodDescriptor parseDescriptor(ServiceDefinition definition, Method method) {
        Class<?>[] parameterTypes = method.getParameterTypes();
        Type requestType = parameterTypes.length > 0 ? parameterTypes[0] : Void.class;
        return MethodDescriptor.newBuilder()
                .setType(MethodDescriptor.MethodType.UNARY)
                .setFullMethodName(method.getDeclaringClass().getName() + "/" + method.getName())
                .setSampledToLocalTracing(true)
                .setRequestMarshaller(marshallerRegister.getMarshaller(requestType))
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
