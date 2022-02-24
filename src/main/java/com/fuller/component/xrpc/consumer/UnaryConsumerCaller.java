package com.fuller.component.xrpc.consumer;

import com.fuller.component.xrpc.convert.TypeConvert;
import com.fuller.component.xrpc.exception.RpcException;
import com.fuller.component.xrpc.parser.MethodParameterParser;
import io.grpc.CallOptions;
import io.grpc.Channel;
import io.grpc.MethodDescriptor;
import io.grpc.stub.ClientCalls;
import lombok.RequiredArgsConstructor;

import java.lang.reflect.Method;

/**
 * @author Allen Huang on 2022/2/23
 */
@RequiredArgsConstructor
public class UnaryConsumerCaller implements ConsumerCaller {

    protected final Method method;
    protected final Channel channel;
    protected final MethodParameterParser parser;
    protected final MethodDescriptor methodDescriptor;
    protected final TypeConvert requestConvert;
    protected final TypeConvert responseConvert;

    @Override
    public Object call(Object[] args) {
        Object value = parser.toRpcValue(method, args);
        Object request = requestConvert.convertTo(value);
        if (request == null) {
            throw new RpcException("请求参数值不能为空.");
        }
        Object response = ClientCalls.blockingUnaryCall(channel.newCall(methodDescriptor, CallOptions.DEFAULT), request);
        return responseConvert.convertFrom(response);
    }
}
