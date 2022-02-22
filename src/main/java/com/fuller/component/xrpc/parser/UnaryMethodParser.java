package com.fuller.component.xrpc.parser;

import com.fuller.component.xrpc.Invoker;
import com.fuller.component.xrpc.MarshallerRegister;
import com.fuller.component.xrpc.ServiceDefinition;
import com.fuller.component.xrpc.consumer.ConsumerChannelFactory;
import com.fuller.component.xrpc.consumer.ClientCaller;
import io.grpc.*;
import io.grpc.stub.ClientCalls;
import io.grpc.stub.ServerCalls;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Method;

/**
 * @author Allen Huang on 2022/2/17
 */
@Slf4j
public abstract class UnaryMethodParser extends BaseMethodParser {

    public UnaryMethodParser(MarshallerRegister marshallerRegister,
                             ConsumerChannelFactory channelFactory) {
        super(marshallerRegister, channelFactory);
    }

    @Override
    protected ServerCallHandler buildServerCallHandler(Method method, Object target) {
        return ServerCalls.asyncUnaryCall(callMethod(target, method));
    }

    protected ServerCalls.UnaryMethod callMethod(Object target, Method method) {
        return new DefaultUnaryMethod(target, method);
    }

    @Override
    protected ClientCaller buildClientCaller(ServiceDefinition definition, Method method) {
        Channel channel = channelFactory.getChannel(definition.getHostname(), definition.getPort());
        MethodDescriptor md = parseDescriptor(definition, method);
        return clientCaller(channel, md);
    }

    protected ClientCaller clientCaller(Channel channel, MethodDescriptor md) {
        return new DefaultClientCaller(channel, md);
    }

    @Override
    protected MethodDescriptor.MethodType getMethodType() {
        return MethodDescriptor.MethodType.UNARY;
    }

    @RequiredArgsConstructor
    protected static class DefaultClientCaller extends Invoker implements ClientCaller {
        private final Channel channel;
        private final MethodDescriptor md;

        @Override
        public Object call(Object[] args) {
            Object response = ClientCalls.blockingUnaryCall(channel, md, CallOptions.DEFAULT, transformRequest(args));
            return transformResponse(response);
        }

        protected Object transformRequest(Object[] args) {
            return args[0];
        }

        protected Object transformResponse(Object response) {
            return response;
        }

    }

    @RequiredArgsConstructor
    protected static class DefaultUnaryMethod extends Invoker implements ServerCalls.UnaryMethod {

        private final Object target;
        private final Method method;

        @Override
        public void invoke(Object request, StreamObserver responseObserver) {
            try {
                Object response = invoke(target, method, transformRequest(request));
                responseObserver.onNext(transformResponse(response));
            } catch (Throwable error) {
                log.error("invoke server error", error);
                responseObserver.onError(error);
            } finally {
                responseObserver.onCompleted();
            }
        }

        protected Object transformRequest(Object request) {
            return request;
        }

        protected Object transformResponse(Object response) {
            return response;
        }

    }

}
