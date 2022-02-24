package com.fuller.component.xrpc.provider;

import com.fuller.component.xrpc.Invoker;
import com.fuller.component.xrpc.convert.TypeConvert;
import com.fuller.component.xrpc.parser.MethodParameterParser;
import io.grpc.ServerCallHandler;
import io.grpc.stub.ServerCalls;
import io.grpc.stub.StreamObserver;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Method;
import java.util.Collections;
import java.util.List;

/**
 * @author Allen Huang on 2022/2/23
 */
@Slf4j
public class ServerAsyncUnaryCallMethod implements ServerCalls.UnaryMethod, ServerInvoker {

    protected final Object target;
    protected final Method method;
    protected final Handler handler;
    protected final TypeConvert requestConvert;
    protected final TypeConvert responseConvert;
    protected final MethodParameterParser parameterParser;

    public ServerAsyncUnaryCallMethod(Object target, Method method,
                                      TypeConvert requestConvert,
                                      TypeConvert responseConvert,
                                      MethodParameterParser parameterParser) {
        this.target = target;
        this.method = method;
        this.requestConvert = requestConvert;
        this.responseConvert = responseConvert;
        this.parameterParser = parameterParser;
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
            List<Object> param = parameterParser.toMethodValue(method, obj);
            Object response = handler.handle(target, method, param);
            Object result = responseConvert.convertTo(response);
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
        Object handle(Object target, Method method, List<Object> args);
    }

    private static class DefaultHandler extends Invoker implements Handler {

        @Override
        public Object handle(Object target, Method method, List<Object> args) {
            return invoke(target, method, args);
        }
    }

    private static class VoidHandler extends Invoker implements Handler {

        @Override
        public Object handle(Object target, Method method, List<Object> args) {
            return invoke(target, method, Collections.emptyList());
        }
    }

}
