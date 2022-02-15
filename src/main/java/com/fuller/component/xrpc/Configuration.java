package com.fuller.component.xrpc;

import com.fuller.component.xrpc.exception.RpcException;
import com.fuller.component.xrpc.parser.MethodParser;
import io.grpc.MethodDescriptor;
import io.grpc.ServerCallHandler;
import io.grpc.ServiceDescriptor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Allen Huang on 2022/2/11
 */
@Component
@RequiredArgsConstructor
@SuppressWarnings("rawtypes")
public class Configuration implements MethodRegister, ServiceRegister {

    private final Map<Method, MethodDescriptor> methodDescriptors = new HashMap<>();
    private final Map<Class<?>, ServiceDescriptor> serviceDescriptors = new HashMap<>();

    private final List<MethodParser> methodParsers;

    @Override
    public MethodDescriptor getMethodDescriptor(ServiceDefinition definition, Method method) {
        MethodDescriptor methodDescriptor = methodDescriptors.get(method);
        if (methodDescriptor == null) {
            synchronized (methodDescriptors) {
                methodDescriptor = methodDescriptors.get(method);
                if (methodDescriptor == null) {
                    for (MethodParser parser : methodParsers) {
                        methodDescriptor = parser.parseDescriptor(definition, method);
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
    public ServerCallHandler getServerCallHandler(ServiceDefinition definition, Method method) {
        return null;
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
