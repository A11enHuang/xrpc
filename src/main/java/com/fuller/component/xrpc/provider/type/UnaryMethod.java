package com.fuller.component.xrpc.provider.type;

import com.fuller.component.xrpc.ServiceDefinition;
import com.fuller.component.xrpc.parser.MethodParser;
import io.grpc.MethodDescriptor;
import io.grpc.ServerCallHandler;
import io.grpc.stub.ServerCalls;

import java.lang.reflect.Method;

/**
 * @author Leo Li on 2022/2/15
 */
public class UnaryMethod implements MethodParser {

    @Override
    public MethodDescriptor parseDescriptor(ServiceDefinition definition, Method method) {
        return null;
    }

    @Override
    public ServerCallHandler parseServerCallHandler(Method method, Object target) {
        return ServerCalls.asyncUnaryCall((request, responseObserver) -> {

        });
    }

}
