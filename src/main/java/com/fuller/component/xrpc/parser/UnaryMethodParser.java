package com.fuller.component.xrpc.parser;

import com.fuller.component.xrpc.MarshallerRegister;
import com.fuller.component.xrpc.ServiceDefinition;
import com.fuller.component.xrpc.channel.ManagedChannelFactory;
import com.fuller.component.xrpc.consumer.ClientCaller;
import io.grpc.CallOptions;
import io.grpc.ManagedChannel;
import io.grpc.MethodDescriptor;
import io.grpc.ServerCallHandler;
import io.grpc.stub.ClientCalls;
import io.grpc.stub.ServerCalls;

import java.lang.reflect.Method;

/**
 * @author Allen Huang on 2022/2/17
 */
public abstract class UnaryMethodParser extends BaseMethodParser {

    public UnaryMethodParser(MarshallerRegister marshallerRegister,
                             ManagedChannelFactory channelFactory) {
        super(marshallerRegister, channelFactory);
    }

    @Override
    protected ServerCallHandler buildServerCallHandler(Method method, Object target) {
        return ServerCalls.asyncUnaryCall((request, responseObserver) -> {
            try {
                Object response = invokeServer(target, method, transformsServerRequest(request));
                responseObserver.onNext(transformServerResponse(response));
            } catch (Throwable error) {
                error.printStackTrace();
                responseObserver.onError(error);
            } finally {
                responseObserver.onCompleted();
            }
        });
    }

    protected Object invokeServer(Object target,Method method,Object request){
        return invoke(target, method, transformsServerRequest(request));
    }

    @Override
    protected ClientCaller buildClientCaller(ServiceDefinition definition, Method method) {
        ManagedChannel channel = channelFactory.getManagedChannel(definition.getAppName(), definition.getPort());
        MethodDescriptor md = parseDescriptor(definition, method);
        return args -> {
            Object response = ClientCalls.blockingUnaryCall(channel, md, CallOptions.DEFAULT, transformsClientRequest(args));
            return transformsClientResponse(response);
        };
    }

    @Override
    protected MethodDescriptor.MethodType getMethodType() {
        return MethodDescriptor.MethodType.UNARY;
    }

    protected Object transformsClientRequest(Object[] args) {
        if (args == null || args.length == 0) {
            throw new IllegalArgumentException("请求参数不能为空或者null");
        }
        return args[0];
    }

    protected Object transformsClientResponse(Object response) {
        return response;
    }

    protected Object transformsServerRequest(Object request) {
        return request;
    }

    protected Object transformServerResponse(Object response) {
        return response;
    }

}
