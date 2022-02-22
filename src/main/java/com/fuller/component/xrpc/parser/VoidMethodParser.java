package com.fuller.component.xrpc.parser;

import com.fuller.component.xrpc.MarshallerRegister;
import com.fuller.component.xrpc.ServiceDefinition;
import com.fuller.component.xrpc.consumer.ConsumerChannelFactory;
import com.fuller.component.xrpc.consumer.ClientCaller;
import io.grpc.Channel;
import io.grpc.MethodDescriptor;
import io.grpc.stub.ServerCalls;
import io.grpc.stub.StreamObserver;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Type;

/**
 * @author Allen Huang on 2022/2/17
 */
@Slf4j
@Component
public class VoidMethodParser extends UnaryMethodParser {

    private static Void INSTANCE;

    static {
        try {
            Constructor<?> constructor = Void.class.getDeclaredConstructors()[0];
            constructor.setAccessible(true);
            INSTANCE = (Void) constructor.newInstance();
        } catch (Exception e) {
            //忽略异常
        }
    }

    public VoidMethodParser(MarshallerRegister marshallerRegister,
                            ConsumerChannelFactory channelFactory) {
        super(marshallerRegister, channelFactory);
    }


    @Override
    protected boolean checkMethod(ServiceDefinition definition, Method method) {
        return method.getReturnType() == void.class && method.getParameters().length == 0;
    }

    @Override
    protected Type getRequestType(ServiceDefinition definition, Method method) {
        return void.class;
    }

    @Override
    protected ServerCalls.UnaryMethod callMethod(Object target, Method method) {
        return new DefaultUnaryMethod(target, method) {
            @Override
            public void invoke(Object request, StreamObserver responseObserver) {
                try {
                    invoke(target, method);
                    responseObserver.onNext(INSTANCE);
                } catch (Throwable error) {
                    log.error("invoke server error", error);
                    responseObserver.onError(error);
                } finally {
                    responseObserver.onCompleted();
                }
            }
        };
    }

    @Override
    protected ClientCaller clientCaller(Channel channel, MethodDescriptor md) {
        return new DefaultClientCaller(channel, md) {
            @Override
            protected Object transformResponse(Object response) {
                return null;
            }

            @Override
            protected Object transformRequest(Object[] args) {
                return INSTANCE;
            }
        };
    }

}
