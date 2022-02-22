package com.fuller.component.xrpc.consumer;

import com.fuller.component.xrpc.MethodRegister;
import com.fuller.component.xrpc.ServiceDefinition;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Allen Huang on 2022/2/22
 */
@Slf4j
@RequiredArgsConstructor
public class DefaultConsumerContext implements ConsumerContext {

    private final MethodRegister methodRegister;

    private final Map<Class<?>, Object> instanceMap = new HashMap<>();

    @Override
    public Object getProxy(ServiceDefinition definition) {
        Class<?> type = definition.getType();
        Object instance = instanceMap.get(type);
        if (instance == null) {
            synchronized (instanceMap) {
                instance = instanceMap.get(type);
                if (instance == null) {
                    Map<Method, ClientCaller> stubs = buildStub(definition);
                    instance = ConsumerProxy.create(type, stubs);
                    log.info("[gRPC][Consumer]{}成功注册{}个RPC方法", type.getName(), stubs.size());
                    instanceMap.put(type, instance);
                }
            }
        }
        return instance;
    }

    protected Map<Method, ClientCaller> buildStub(ServiceDefinition definition) {
        Class<?> type = definition.getType();
        Map<Method, ClientCaller> map = new HashMap<>();
        for (Class<?> parent = type; parent != null; parent = parent.getSuperclass()) {
            for (Method method : parent.getDeclaredMethods()) {
                map.put(method, methodRegister.getClientCaller(definition, method));
            }
        }
        return map;
    }

}
