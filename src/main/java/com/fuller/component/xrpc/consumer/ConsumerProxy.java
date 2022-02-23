package com.fuller.component.xrpc.consumer;

import com.fuller.component.xrpc.MethodRegister;
import com.fuller.component.xrpc.exception.RpcException;
import com.fuller.component.xrpc.util.ClassLoaderUtil;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Map;

/**
 * @author Allen Huang on 2022/2/23
 */
public class ConsumerProxy implements InvocationHandler {

    private final Map<Method, ConsumerCaller> stubs;
    private final Class<?> target;

    public ConsumerProxy(Class<?> target, Map<Method, ConsumerCaller> stubs) {
        this.stubs = stubs;
        this.target = target;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        ConsumerCaller caller = stubs.get(method);
        if (caller != null) {
            return caller.call(args);
        }
        Class<?> declaringClass = method.getDeclaringClass();
        //object方法
        if (Object.class.equals(declaringClass)) {
            return method.invoke(this, args);
        }
        throw new RpcException("调用RPC异常，未匹配到对应的方法.");
    }

    public static <T> T create(Class<T> type, Map<Method, ConsumerCaller> stubs) {
        ClassLoader classLoader = ClassLoaderUtil.getClassLoader();
        ConsumerProxy proxy = new ConsumerProxy(type, stubs);
        return (T) Proxy.newProxyInstance(classLoader, new Class<?>[]{type}, proxy);
    }

}
