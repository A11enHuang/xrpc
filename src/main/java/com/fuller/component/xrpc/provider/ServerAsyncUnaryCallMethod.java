package com.fuller.component.xrpc.provider;

import com.fuller.component.xrpc.Invoker;
import com.fuller.component.xrpc.convert.TypeConvert;
import io.grpc.ServerCallHandler;
import io.grpc.stub.ServerCalls;
import io.grpc.stub.StreamObserver;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Method;

/**
 * @author Allen Huang on 2022/2/23
 */
@Slf4j
public class ServerAsyncUnaryCallMethod implements ServerCalls.UnaryMethod, ServerInvoker {

    protected final Object target;
    protected final Method method;
    protected final TypeConvert requestConvert;
    protected final TypeConvert responseConvert;
    protected final Handler handler;

    public ServerAsyncUnaryCallMethod(Object target, Method method,
                                      TypeConvert requestConvert,
                                      TypeConvert responseConvert) {
        this.target = target;
        this.method = method;
        this.requestConvert = requestConvert;
        this.responseConvert = responseConvert;
        if (method.getParameterCount() == 0) {
            this.handler = new VoidHandler();
        } else {
            this.handler = new DefaultHandler();
        }
    }

    @Override
    public void invoke(Object request, StreamObserver responseObserver) {
        try {
            Object obj = requestConvert.convertFrom(request);
            Object response = handler.handle(target, method, obj);
            Object result = null;
            if (response != null) {
                result = responseConvert.convertFrom(response);
            }
            responseObserver.onNext(result);
        } catch (Exception e) {
            log.error("gRPC服务端执行出错", e);
            responseObserver.onError(e);
        } finally {
            responseObserver.onCompleted();
        }
    }

    @Override
    public ServerCallHandler getServerCallHandler() {
        return ServerCalls.asyncUnaryCall(this);
    }

    private interface Handler {
        Object handle(Object target, Method method, Object args);
    }

    private static class DefaultHandler extends Invoker implements Handler {

        @Override
        public Object handle(Object target, Method method, Object args) {
            return invoke(target, method, args);
        }
    }

    private static class VoidHandler extends Invoker implements Handler {

        @Override
        public Object handle(Object target, Method method, Object args) {
            return invoke(target, method);
        }
    }

}
