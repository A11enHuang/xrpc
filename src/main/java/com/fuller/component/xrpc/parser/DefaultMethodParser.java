package com.fuller.component.xrpc.parser;

import com.fuller.component.xrpc.MarshallerRegister;
import com.fuller.component.xrpc.ServiceDefinition;
import com.fuller.component.xrpc.consumer.ConsumerChannelFactory;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.lang.reflect.Type;

/**
 * 只有一个请求参数
 * 响应不为void
 *
 * @author Allen Huang on 2022/2/17
 */
@Component
public class DefaultMethodParser extends UnaryMethodParser {

    public DefaultMethodParser(MarshallerRegister marshallerRegister,
                               ConsumerChannelFactory channelFactory) {
        super(marshallerRegister, channelFactory);
    }

    @Override
    protected boolean checkMethod(ServiceDefinition definition, Method method) {
        return method.getReturnType() != void.class && method.getParameters().length == 1;
    }

    @Override
    protected Type getRequestType(ServiceDefinition definition, Method method) {
        return method.getParameters()[0].getParameterizedType();
    }


    @Override
    public int getOrder() {
        return Integer.MAX_VALUE - 10;
    }
}
