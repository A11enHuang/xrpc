package com.fuller.component.xrpc;

import com.fuller.component.xrpc.consumer.ClientCaller;
import com.fuller.component.xrpc.exception.RpcException;
import com.fuller.component.xrpc.parser.MethodParser;
import io.grpc.MethodDescriptor;
import io.grpc.ServerCallHandler;
import io.grpc.ServiceDescriptor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
@Slf4j
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
    public ServerCallHandler getServerCallHandler(ServiceDefinition definition, Method method, Object target) {
        for (MethodParser parser : methodParsers) {
            ServerCallHandler serverCallHandler = parser.parseServerCallHandler(method, target);
            if (serverCallHandler != null) {
                return serverCallHandler;
            }
        }
        throw new RpcException("找不到服务调用类型." + definition.getType().getName() + "#" + method.getName());
    }

    @Override
    public ClientCaller getClientCaller(ServiceDefinition definition, Method method) {
        for (MethodParser parser : methodParsers) {
            ClientCaller clientCaller = parser.parseClientCaller(definition, method);
            if (clientCaller != null) {
                return clientCaller;
            }
        }
        throw new RpcException("无法为目标方法创建客户端存根." + definition.getType().getName() + "#" + method.getName());
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
