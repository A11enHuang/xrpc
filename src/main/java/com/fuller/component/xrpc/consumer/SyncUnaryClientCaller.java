package com.fuller.component.xrpc.consumer;

import io.grpc.CallOptions;
import io.grpc.Channel;
import io.grpc.MethodDescriptor;
import io.grpc.stub.AbstractBlockingStub;
import io.grpc.stub.ClientCalls;

/**
 * 同步的一元RPC
 *
 * @author Allen Huang on 2022/2/11
 */
@SuppressWarnings("rawtypes")
public class SyncUnaryClientCaller extends AbstractBlockingStub implements ClientCaller {

    private MethodDescriptor methodDescriptor;

    protected SyncUnaryClientCaller(Channel channel, CallOptions callOptions) {
        super(channel, callOptions);
    }

    @Override
    protected SyncUnaryClientCaller build(Channel channel, CallOptions callOptions) {
        return new SyncUnaryClientCaller(channel, callOptions);
    }

    @Override
    public Object call(Object[] args) {
        return ClientCalls.blockingUnaryCall(getChannel(), methodDescriptor, getCallOptions(), null);
    }

    public void setMethodDescriptor(MethodDescriptor methodDescriptor) {
        this.methodDescriptor = methodDescriptor;
    }
}
