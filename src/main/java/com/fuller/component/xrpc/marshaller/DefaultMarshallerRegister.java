package com.fuller.component.xrpc.marshaller;

import io.grpc.MethodDescriptor;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Allen Huang on 2022/2/15
 */
@SuppressWarnings("rawtypes")
public class DefaultMarshallerRegister implements MarshallerRegister {

    private final Map<Type, MethodDescriptor.Marshaller> marshallerMap = new HashMap<>();

    private final MarshallerFactory defaultFactory;

    public DefaultMarshallerRegister(MarshallerFactory defaultFactory) {
        this.defaultFactory = defaultFactory;
        //这里可以初始化一些默认的序列化
        this.registerMarshaller(String.class, new StringMarshaller());
        this.registerMarshaller(void.class, new VoidMarshaller());
        this.registerMarshaller(Void.class, new VoidMarshaller());
    }

    @Override
    public void registerMarshaller(Type type, MethodDescriptor.Marshaller marshaller) {
        synchronized (marshallerMap) {
            marshallerMap.put(type, marshaller);
        }
    }

    @Override
    public MethodDescriptor.Marshaller getMarshaller(Type type) {
        MethodDescriptor.Marshaller marshaller = marshallerMap.get(type);
        if (marshaller == null) {
            synchronized (marshallerMap) {
                marshaller = marshallerMap.get(type);
                if (marshaller == null) {
                    marshaller = defaultFactory.getMarshaller(type);
                    marshallerMap.put(type, marshaller);
                }
            }
        }
        return marshaller;
    }

}
