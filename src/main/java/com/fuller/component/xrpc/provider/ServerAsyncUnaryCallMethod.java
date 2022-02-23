package com.fuller.component.xrpc.provider;

import com.fuller.component.xrpc.Invoker;
import com.fuller.component.xrpc.convert.TypeConvert;
import io.grpc.ServerCallHandler;
import io.grpc.stub.ServerCalls;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;

import java.lang.reflect.Method;

/**
 * @author Allen Huang on 2022/2/23
 */
@RequiredArgsConstructor
public class ServerAsyncUnaryCallMethod extends Invoker implements ServerCalls.UnaryMethod, ServerInvoker {

    protected final Object target;
    protected final Method method;
    protected final TypeConvert requestConvert;
    protected final TypeConvert responseConvert;

    @Override
    public void invoke(Object request, StreamObserver responseObserver) {
        try {
            Object obj = requestConvert.convertFrom(request);
            Object response = invoke(target, method, obj);
            Object result = null;
            if (response != null) {
                result = responseConvert.convertFrom(response);
            }
            responseObserver.onNext(result);
        } catch (Exception e) {
            responseObserver.onError(e);
        } finally {
            responseObserver.onCompleted();
        }
    }

    @Override
    public ServerCallHandler getServerCallHandler() {
        return ServerCalls.asyncUnaryCall(this);
    }

}
