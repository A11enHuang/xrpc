package com.fuller.component.xrpc.register;

import com.fuller.component.xrpc.ServiceDefinition;
import com.fuller.component.xrpc.exception.RpcException;
import com.fuller.component.xrpc.parser.MethodParser;
import io.grpc.MethodDescriptor;
import io.grpc.ServiceDescriptor;
import lombok.RequiredArgsConstructor;

import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Allen Huang on 2022/2/11
 */
@RequiredArgsConstructor
@SuppressWarnings("rawtypes")
public class Configuration implements MethodDescriptorRegister, MarshallerRegister, ServiceRegister {

    private final Map<Method, MethodDescriptor> methodDescriptors = new HashMap<>();
    private final Map<Class<?>, ServiceDescriptor> serviceDescriptors = new HashMap<>();
    private final Map<Type, MethodDescriptor.Marshaller> marshallerMap = new HashMap<>();

    private final List<MethodParser> methodParsers;
    private final MarshallerFactory marshallerFactory;

    @Override
    public MethodDescriptor getMethodDescriptor(ServiceDefinition definition, Method method) {
        MethodDescriptor methodDescriptor = methodDescriptors.get(method);
        if (methodDescriptor == null) {
            synchronized (methodDescriptors) {
                methodDescriptor = methodDescriptors.get(method);
                if (methodDescriptor == null) {
                    for (MethodParser factory : methodParsers) {
                        methodDescriptor = factory.parseDescriptor(definition, method);
                        if (methodDescriptor != null) {
                            methodDescriptors.put(method, methodDescriptor);
                            break;
                        }
                    }
                }
            }
        }
        if (methodDescriptor == null) {
            throw new RpcException("方法无法被解析." + definition.getType().getName() + "#" + method.getName());
        }
        return methodDescriptor;
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
                    marshaller = marshallerFactory.getMarshaller(type);
                    marshallerMap.put(type, marshaller);
                }
            }
        }
        return marshaller;
    }

    @Override
    public ServiceDescriptor getServiceDescriptor(ServiceDefinition definition) {
        Class<?> type = definition.getType();
        ServiceDescriptor descriptor = serviceDescriptors.get(type);
        if (descriptor == null) {
            synchronized (this.serviceDescriptors) {
                descriptor = serviceDescriptors.get(type);
                if (descriptor == null) {
                    ServiceDescriptor.Builder builder = ServiceDescriptor.newBuilder(definition.getServiceName());
                    for (Method method : type.getDeclaredMethods()) {
                        builder.addMethod(getMethodDescriptor(definition, method));
                    }
                    descriptor = builder.build();
                    serviceDescriptors.put(type, descriptor);
                }
            }
        }
        return descriptor;
    }

}
