package com.fuller.component.xrpc.consumer;

import lombok.RequiredArgsConstructor;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.Map;

/**
 * 在消费端所有的接口将会生成Invoker代理对象。
 * 除Object方法外，Invoker对象会将所有的RPC方法都委托给ClientCaller去完成实际的RPC调用
 *
 * @author Allen Huang on 2022/2/11
 */
@RequiredArgsConstructor
public class ConsumerInvoker implements InvocationHandler {

    private final Map<Method, ClientCaller> callerMap;

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        ClientCaller caller = callerMap.get(method);
        if (caller != null) {
            return caller.call(args);
        }
        Class<?> declaringClass = method.getDeclaringClass();
        //object方法
        if (Object.class.equals(declaringClass)) {
            return method.invoke(this, args);
        }
        //TODO: 这里应该要抛出异常
        return null;
    }

}
