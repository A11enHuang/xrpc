package com.fuller.component.xrpc.parser;

import com.fuller.component.xrpc.MarshallerRegister;
import com.fuller.component.xrpc.ServiceDefinition;
import com.fuller.component.xrpc.channel.ManagedChannelFactory;
import org.springframework.stereotype.Component;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Type;

/**
 * @author Allen Huang on 2022/2/17
 */
@Component
public class VoidMethodParser extends UnaryMethodParser {

    private static Void INSTANCE;
    private static final Object[] EMPTY = new Object[0];

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
                            ManagedChannelFactory channelFactory) {
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
    protected Object invokeServer(Object target, Method method, Object request) {
        return invoke(target, method);
    }

    @Override
    protected Object transformsClientRequest(Object[] args) {
        return INSTANCE;
    }

    @Override
    protected Object transformServerResponse(Object response) {
        return null;
    }

    @Override
    protected Object transformsServerRequest(Object request) {
        return INSTANCE;
    }

    @Override
    protected Object transformsClientResponse(Object response) {
        return null;
    }

    @Override
    public int getOrder() {
        return -100;
    }
}
