package com.fuller.component.xrpc.consumer;

import com.fuller.component.xrpc.MethodRegister;
import com.fuller.component.xrpc.ServiceDefinition;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Allen Huang on 2022/2/23
 */
@Slf4j
@RequiredArgsConstructor
public class DefaultConsumerContext implements ConsumerContext {

    private final Map<Class<?>, Object> proxyMap = new HashMap<>();

    protected final MethodRegister methodRegister;

    @Override
    public <T> T getProxy(ServiceDefinition definition) {
        Class<?> target = definition.getType();
        Object proxy = proxyMap.get(target);
        if (proxy == null) {
            synchronized (proxyMap) {
                proxy = proxyMap.computeIfAbsent(target, k -> getProxyInstance(definition));
            }
        }
        return (T) proxy;
    }

    private <T> T getProxyInstance(ServiceDefinition definition) {
        Class<?> target = definition.getType();
        Map<Method, ConsumerCaller> stubs = new HashMap<>();
        for (Method method : target.getDeclaredMethods()) {
            ConsumerCaller caller = methodRegister.parseConsumerCaller(definition, method);
            stubs.put(method, caller);
        }
        log.info("[gRPC][Consumer]{}成功注册{}个RPC方法", target.getName(), stubs.size());
        return (T) ConsumerProxy.create(target, stubs);
    }

}
