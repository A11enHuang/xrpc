package com.fuller.component.xrpc.parser;

import com.fuller.component.xrpc.MarshallerRegister;
import com.fuller.component.xrpc.ServiceDefinition;
import io.grpc.MethodDescriptor;
import io.grpc.ServerCallHandler;
import io.grpc.stub.ServerCalls;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.lang.reflect.Type;

/**
 * 无参数且无返回值
 *
 * @author Allen Huang on 2022/2/16
 */
@Component
public class VoidMethodParser extends InvokeMethodParser {

    public VoidMethodParser(MarshallerRegister marshallerRegister) {
        super(marshallerRegister);
    }

    @Override
    protected ServerCallHandler buildServerCallHandler(Method method, Object target) {
        return ServerCalls.asyncUnaryCall((request, responseObserver) -> {
            try {
                invoke(target, method, null);
                responseObserver.onNext(null);
            } finally {
                responseObserver.onCompleted();
            }
        });
    }

    @Override
    protected boolean checkMethod(ServiceDefinition definition, Method method) {
        return method.getParameters().length == 0 && method.getReturnType() == void.class;
    }

    @Override
    protected MethodDescriptor.MethodType getMethodType() {
        return MethodDescriptor.MethodType.UNARY;
    }

    @Override
    protected Type getRequestType(ServiceDefinition definition, Method method) {
        return void.class;
    }

    @Override
    public int getOrder() {
        return -1;
    }
}
