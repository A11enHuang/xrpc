package com.fuller.component.xrpc;

import com.fuller.component.xrpc.exception.RpcException;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author Allen Huang on 2022/2/17
 */
public class Invoker {

    private static final Map<Method, MethodInvoker> methodCache = new ConcurrentHashMap<>();

    protected Object invoke(Object instance, Method method, Object... args) {
        try {
            return cachedInvoker(method).invoke(instance, method, args);
        } catch (Throwable e) {
            throw new RpcException("执行RPC方法失败.", e);
        }
    }

    private MethodInvoker cachedInvoker(Method method) throws Throwable {
        try {
            return methodCache.computeIfAbsent(method, m -> {
                try {
                    return new DefaultMethodInvoker(getMethodHandle(method));
                } catch (IllegalAccessException | InvocationTargetException
                        | NoSuchMethodException e) {
                    throw new RuntimeException(e);
                }
            });
        } catch (RuntimeException re) {
            Throwable cause = re.getCause();
            throw cause == null ? re : cause;
        }
    }

    private MethodHandle getMethodHandle(Method method)
            throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        final Class<?> declaringClass = method.getDeclaringClass();
        MethodType methodType;
        Parameter[] parameters = method.getParameters();
        if (parameters.length == 0) {
            methodType = MethodType.methodType(method.getReturnType());
        } else {
            List<Class<?>> paramTypes = Stream.of(parameters)
                    .map(Parameter::getType)
                    .collect(Collectors.toList());
            methodType = MethodType.methodType(method.getReturnType(), paramTypes);
        }
        return MethodHandles.lookup().findVirtual(declaringClass, method.getName(), methodType);
    }


    interface MethodInvoker {
        Object invoke(Object instance, Method method, Object[] args) throws Throwable;
    }

    private static class DefaultMethodInvoker implements MethodInvoker {
        private final MethodHandle methodHandle;

        public DefaultMethodInvoker(MethodHandle methodHandle) {
            super();
            this.methodHandle = methodHandle;
        }

        @Override
        public Object invoke(Object instance, Method method, Object... args) throws Throwable {
            return methodHandle.bindTo(instance).invokeWithArguments(args);
        }
    }
}
